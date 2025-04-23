package com.capybara.springboot.storage.db.core.config;

import com.capybara.springboot.storage.db.core.JusoRoadCode;
import com.capybara.springboot.storage.db.core.batch.JdbcBatchWriter;
import com.capybara.springboot.storage.db.core.batch.JusoRoadCodeItemProcessor;
import com.capybara.springboot.storage.db.core.batch.JusoRoadCodeItemReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class BatchConfig {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private DataSource dataSource;

    @Bean
    public JusoRoadCodeItemReader jusoRoadCodeItemReader() throws Exception {
        JusoRoadCodeItemReader reader = new JusoRoadCodeItemReader();
        reader.setResource(new ClassPathResource("개선_도로명코드_전체분.txt"));
        reader.setEncoding("MS949");
        reader.afterPropertiesSet();
        return reader;
    }

    @Bean
    public JusoRoadCodeItemProcessor jusoRoadCodeItemProcessor() {
        return new JusoRoadCodeItemProcessor();
    }

    @Bean
    public JdbcBatchWriter jdbcBatchWriter() {
        return new JdbcBatchWriter();
    }

    @Bean
    public Job importJusoRoadCodeJob() throws Exception {
        return new JobBuilder("importJusoRoadCodeJob", jobRepository)
                .start(step1())
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(25);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("batch-");
//        executor.initialize();
        return executor;
    }

    @Bean
    public Step step1() throws Exception {
        return new StepBuilder("step1", jobRepository)
                .<String[], JusoRoadCode>chunk(1000, transactionManager) // 청크 크기 증가
                .reader(jusoRoadCodeItemReader())
                .processor(jusoRoadCodeItemProcessor())
                .writer(jdbcBatchWriter())
                .taskExecutor(taskExecutor()) // 멀티스레드 처리 추가
                .build();
    }

}

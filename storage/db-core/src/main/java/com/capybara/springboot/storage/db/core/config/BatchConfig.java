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
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
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

    private static final String INPUT_FILE_PATH = "개선_도로명코드_전체분.txt"; // 입력 파일 경로 상수화

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

    // --- 파티셔닝 관련 Bean 정의는 제거됨 ---

    @Bean
    public Job importJusoRoadCodeJob() throws Exception {
        return new JobBuilder("importJusoRoadCodeJob", jobRepository)
                .start(step1()) // 원래 Step 실행
                .build();
    }

//    @Bean
//    public TaskExecutor taskExecutor() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(25);
//        executor.setMaxPoolSize(50);
//        executor.setQueueCapacity(100);
//        executor.setThreadNamePrefix("batch-");
//        executor.initialize();
//        return executor;
//    }


    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 스레드 풀 크기는 CPU 코어 수, DB 커넥션 풀 크기 등을 고려하여 조절
        // 너무 많으면 오히려 컨텍스트 스위칭, 리소스 경쟁으로 느려질 수 있음
        // 예: 4 ~ 16 사이에서 테스트 시작
        executor.setCorePoolSize(8);
        executor.setMaxPoolSize(16);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("batch-");
        executor.initialize();
        return executor;
    }

    // --- 파티셔닝 관련 Step 정의는 제거됨 ---

    // --- 원래의 Multi-threaded Step ---
    @Bean
    public Step step1() throws Exception {
        return new StepBuilder("step1", jobRepository)
                .<String[], JusoRoadCode>chunk(5000, transactionManager) // 청크 크기 유지
                .reader(flatFileItemReader()) // StepScope 없는 Reader 사용
                .processor(jusoRoadCodeItemProcessor()) // Processor는 그대로 사용
                .writer(jdbcBatchItemWriter()) // StepScope 없는 Writer 사용
                .taskExecutor(taskExecutor()) // 멀티스레드 처리 적용
                .build();
    }


    @Bean
    public JdbcBatchItemWriter<JusoRoadCode> jdbcBatchItemWriter() {
        // JusoRoadCode 객체의 필드 이름을 기반으로 파라미터 매핑
        BeanPropertyItemSqlParameterSourceProvider<JusoRoadCode> itemSqlParameterSourceProvider =
                new BeanPropertyItemSqlParameterSourceProvider<>();

        // 주의: 테이블 컬럼명과 JusoRoadCode 필드명이 일치해야 함 (혹은 SQL에 AS 사용)
        // 주의: juso_road_code_id는 Processor에서 생성되므로 포함
        String sql = "INSERT INTO juso_road_code (juso_road_code_id, road_name_code, road_name, road_name_roman, emdong_serial_no, " +
                "sido, sido_roman, sigungu, sigungu_roman, emdong_name, emdong_roman, emdong_type, " +
                "emdong_code, is_used, change_reason, change_history_info, announcement_date, deletion_date) " +
                "VALUES (:jusoRoadCodeId, :roadNameCode, :roadName, :roadNameRoman, :emdongSerialNo, " +
                ":sido, :sidoRoman, :sigungu, :sigunguRoman, :emdongName, :emdongRoman, :emdongType, " +
                ":emdongCode, :isUsed, :changeReason, :changeHistoryInfo, :announcementDate, :deletionDate)";

        return new JdbcBatchItemWriterBuilder<JusoRoadCode>()
                .itemSqlParameterSourceProvider(itemSqlParameterSourceProvider)
                .sql(sql)
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public FlatFileItemReader<String[]> flatFileItemReader() {
        // 버퍼 크기 조절을 위한 커스텀 팩토리
        CustomBufferSizeBufferedReaderFactory bufferedReaderFactory =
                new CustomBufferSizeBufferedReaderFactory(161920); // 158KB

        return new FlatFileItemReaderBuilder<String[]>()
                .name("jusoRoadCodeFileReader")
                .resource(new ClassPathResource(INPUT_FILE_PATH))
                .encoding("MS949") // 파일 인코딩 정확히 지정
                .linesToSkip(0) // 헤더 없음
                .bufferedReaderFactory(bufferedReaderFactory) // 버퍼 크기 설정
                .saveState(false) // 멀티스레드 환경 필수 (파티셔닝 아니어도 필요)
                .strict(true) // 파일이 없거나 읽을 수 없을 때 예외 발생
                .lineMapper((line, lineNumber) -> line.split("\\|", -1)) // 직접 LineMapper 구현
                .build();
    }

    // Processor는 상태가 없으면 StepScope가 필요 없을 수 있음
    // @Bean
    // @StepScope
    // public JusoRoadCodeItemProcessor jusoRoadCodeItemProcessor() {
    //     return new JusoRoadCodeItemProcessor();
    // }
}

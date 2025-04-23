package com.capybara.springboot.storage.db.core.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class JusoRoadCodeBatchJobRunner {
    private static final Logger logger = LoggerFactory.getLogger(JusoRoadCodeBatchJobRunner.class);

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job importJusoRoadCodeJob;

    public void runBatchJob() throws Exception {
        long startTime = System.currentTimeMillis();
        
        // 날짜 형식을 이용하여 고유한 파라미터 생성
        String dateParam = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss"));
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("time", dateParam)
                .toJobParameters();

        logger.info("배치 작업 시작: {}", dateParam);
        JobExecution execution = jobLauncher.run(importJusoRoadCodeJob, jobParameters);
        
        long endTime = System.currentTimeMillis();
        logger.info("배치 작업 완료: {}, 상태: {}, 소요시간: {}ms", 
                dateParam, execution.getStatus(), (endTime - startTime));
    }
}

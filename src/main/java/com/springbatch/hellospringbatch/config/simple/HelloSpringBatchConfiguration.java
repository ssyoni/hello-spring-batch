package com.springbatch.hellospringbatch.config.simple;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class HelloSpringBatchConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private static final String JOB_NAME = "helloJob";
    private static final String STEP_1_NAME = "helloStep";
    private static final String MESSAGE = "Hello, Spring Batch!!!";

    @Bean
    public Job helloJob() {
        return jobBuilderFactory.get(JOB_NAME)
                .start(helloStep())
                .build();
    }

    // 의존성 주입을 통한 JobParameter
    private final SimpleJobTasklet simpleJobTasklet;
    @Bean
    public Step helloStep() {
        return stepBuilderFactory.get(STEP_1_NAME)
                .tasklet(simpleJobTasklet).build();
    }
}

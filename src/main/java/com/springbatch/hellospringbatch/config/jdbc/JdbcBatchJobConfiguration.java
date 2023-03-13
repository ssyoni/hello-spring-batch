package com.springbatch.hellospringbatch.config.jdbc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JdbcBatchJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;
    private final JobCompletionNotificationListener jobCompletionNotificationListner;

    private static final String JOB_NAME = "jobjdbc";
    private static final String STEP_NAME = "jdbcStep";
    private static final int CHUNK_SIZE = 1;

    @Bean
    public Job jdbcJob(){
        return this.jobBuilderFactory.get(JOB_NAME)
                .listener(jobCompletionNotificationListner)
                .start(jdbcStep())
                .build();
    }

    @Bean
    public Step jdbcStep(){
        return this.stepBuilderFactory.get(STEP_NAME)
                .<Customer, Customer>chunk(CHUNK_SIZE)
                .reader(jdbcCursorItemReader())
                .processor((ItemProcessor) jdbcItemProcessor())
                .writer(jdbcItemWriter(dataSource))
                .build();
    }

    @Bean
    public JdbcCursorItemReader jdbcCursorItemReader(){
        return new JdbcCursorItemReaderBuilder<Customer>()
                .fetchSize(CHUNK_SIZE)
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(Customer.class))
                .sql("SELECT id, name, age FROM customers")
                .name("jdbcCursorItemReader")
                .build();
    }

    @Bean
    public CustomerItemProcessor jdbcItemProcessor(){
        return new CustomerItemProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<Customer> jdbcItemWriter(DataSource dataSource){
        return new JdbcBatchItemWriterBuilder<Customer>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .dataSource(dataSource)
                .sql("UPDATE customers SET name = :name, age = :age WHERE id = :id")
                .build();
    }
}

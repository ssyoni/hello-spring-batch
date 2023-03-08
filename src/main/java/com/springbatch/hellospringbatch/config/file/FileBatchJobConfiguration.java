package com.springbatch.hellospringbatch.config.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.math.BigDecimal;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FileBatchJobConfiguration {
    public static final int CHUNK_SIZE = 2;    // 몇개씩 처리할 것인지
    public static final int ADD_PRICE = 1000;
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private static final String JOB_NAME = "fileJob";
    private static final String STEP_NAME = "stepJob";

    private Resource inputFileResource = new FileSystemResource("input/sample-product.csv");
    private Resource outputFileResource = new FileSystemResource("output/output-product.csv");

    @Bean
    public Job fileJob(){
        return this.jobBuilderFactory.get(JOB_NAME)
                .incrementer(new RunIdIncrementer()) // 동일한 파라미터로 재실행할 수 있게 (데이터 갱신)
                .start(fileStep())
                .build();
    }

    @Bean
    public Step fileStep() {
        return this.stepBuilderFactory.get(STEP_NAME)
                .<Product, Product>chunk(CHUNK_SIZE)    // chunk 정의 : Product로 입력받아서 Product로 출력
                .reader(fileItemReader())
                .processor(fileItemProcessor())
                .writer(fileItemWriter())
                .build();
    }


    @Bean
    public FlatFileItemReader fileItemReader(){
        // 아이템 리더 생성
        FlatFileItemReader<Product> productFlatFileItemReader = new FlatFileItemReader<Product>();
        productFlatFileItemReader.setResource(this.inputFileResource);  // 읽을 파일 할당

        // 콤마 기준으로 라인 가져오기(파일 가져오는 정책 정의)
        DefaultLineMapper<Product> lineMapper = new DefaultLineMapper<Product>();
        lineMapper.setLineTokenizer(new DelimitedLineTokenizer());
        lineMapper.setFieldSetMapper(new ProductFieldSetMapper());

        // 파일 정책(lineMapper)에 따라서 파일을 잘라서 가져옴
        productFlatFileItemReader.setLineMapper(lineMapper);
        return productFlatFileItemReader;
    }


    @Bean
    public ItemProcessor<Product, Product> fileItemProcessor() {
        // In: Product --> Out: Product
        return product -> {
            BigDecimal updatedPrice = product.getPrice().add(new BigDecimal(ADD_PRICE));
            log.info("[ItemProcessor] Updated product price - {}", updatedPrice);
            product.setPrice(updatedPrice);
            return product;
        };
    }

    @Bean
    public FlatFileItemWriter<Product> fileItemWriter(){
        FlatFileItemWriter flatFileItemWriter = new FlatFileItemWriter();
        flatFileItemWriter.setResource(outputFileResource);
        flatFileItemWriter.setAppendAllowed(true);  // 파일 쓸 떄 추가로 쓰기

        DelimitedLineAggregator<Product> lineAggregator = new DelimitedLineAggregator<>();  // 라인 규칙

        // 객체에 있는 값을 어떻게 write 할 건지에 대한 정책
        lineAggregator.setFieldExtractor(new BeanWrapperFieldExtractor<>(){
            {
                setNames(new String[]{"id","name","price"});    // product 객체에 있는 필드 값 추출
            }
        });
        flatFileItemWriter.setLineAggregator(lineAggregator);

        return flatFileItemWriter;
    }


}

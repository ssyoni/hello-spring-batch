package com.springbatch.hellospringbatch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication
public class HelloSpringBatchApplication {
	public static void main(String[] args) {
		/* Job이 중단되면 애플리케이션도 중단 */
		System.exit(
				SpringApplication.exit(
						SpringApplication.run(HelloSpringBatchApplication.class, args)
				)
		);
	}
}

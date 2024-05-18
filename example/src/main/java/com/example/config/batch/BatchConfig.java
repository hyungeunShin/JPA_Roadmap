package com.example.config.batch;

import com.example.stock.domain.Stock;
import com.example.stock.dto.APIResponseDTO;
import com.example.stock.repository.StockJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {
    private final StockJpaRepository repository;

    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job stockJob(JobRepository jobRepository) {
        return new JobBuilder("stockJob", jobRepository)
                .start(stockStep(jobRepository))
                .build();
    }

    @Bean
    public Step stockStep(JobRepository jobRepository) {
        return new StepBuilder("stockStep", jobRepository)
                .<APIResponseDTO, List<Stock>>chunk(10, transactionManager)
                .reader(apiItemReader())
                .processor(apiItemProcessor())
                .writer(apiItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<APIResponseDTO> apiItemReader() {
        return new StockItemReader();
    }

    @Bean
    public ItemProcessor<APIResponseDTO, List<Stock>> apiItemProcessor() {
        return new StockItemProcessor();
    }

    @Bean
    public ItemWriter<List<Stock>> apiItemWriter() {
        return new StockItemWriter(repository);
    }
}

package com.arun.spring_batch.config;

import com.arun.spring_batch.model.Products;
import com.arun.spring_batch.mongo.model.MongoProducts;
import com.arun.spring_batch.repository.MongoProductsRepository;
import com.arun.spring_batch.repository.ProductsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Collections;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class BatchConfig {

    private final ProductsRepository productsRepository;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    // Mongo
    private final MongoProductsRepository mongoRepository;


    //Reader
    @Bean
    public FlatFileItemReader<Products> itemReader() {
        FlatFileItemReader<Products> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new ClassPathResource("products.csv"));
        itemReader.setName("csvReader");
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(lineMapper());
        return itemReader;
    }
    // Read from MySQL
    @Bean
    public RepositoryItemReader<Products> mySQLItemReader() {
        RepositoryItemReader<Products> reader = new RepositoryItemReader<>();
        reader.setRepository(productsRepository);
        reader.setMethodName("findAll");
        reader.setPageSize(10);  //Pagination to avoid DB crash
        reader.setSort(Collections.singletonMap("id", Sort.Direction.ASC));
        return reader;
    }


    //Processor
    @Bean
    public ProductsProcessor processor() {
        return new ProductsProcessor();
    }

    // Process while migrate Database
    @Bean
    public MongoProductsProcessor mongoItemProcessor() {
        return new MongoProductsProcessor();
    }

    //Writer
    @Bean
    public RepositoryItemWriter<Products> writer() {
        RepositoryItemWriter<Products> writer = new RepositoryItemWriter<>();
        writer.setRepository(productsRepository);
        writer.setMethodName("save");
        return writer;
    }

    // Now write to Mongo
    @Bean
    public RepositoryItemWriter<MongoProducts> mongoItemWriter() {
        RepositoryItemWriter<MongoProducts> writer = new RepositoryItemWriter<>();
        writer.setRepository(mongoRepository);
        writer.setMethodName("save");
        return writer;
    }


    // Step
    @Bean
    public Step importStep() {
        return new StepBuilder("csvImport", jobRepository)
                .<Products, Products>chunk(10, platformTransactionManager)
                .reader(itemReader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    // Step --> start DB migrate
    @Bean
    public Step migrateDB() {
        return new StepBuilder("migrateToMongo", jobRepository)
                .<Products, MongoProducts>chunk(10, platformTransactionManager)
                .reader(mySQLItemReader())
                .processor(mongoItemProcessor())
                .writer(mongoItemWriter())
                .build();
    }

    //Job
    @Bean
    public Job runJob() {
        return new JobBuilder("importProducts", jobRepository)
                .start(importStep())
                .next(migrateDB())
                .build();
    }

    private LineMapper<Products> lineMapper() {
        DefaultLineMapper<Products> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id","product_name","price","quantity");

        BeanWrapperFieldSetMapper<Products> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Products.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }
}

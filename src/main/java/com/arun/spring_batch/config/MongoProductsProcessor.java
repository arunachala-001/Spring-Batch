package com.arun.spring_batch.config;

import com.arun.spring_batch.model.Products;
import com.arun.spring_batch.mongo.model.MongoProducts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class MongoProductsProcessor implements ItemProcessor<Products, MongoProducts> {


    @Override
    public MongoProducts process(Products products) throws Exception {
        log.info("Data Processing from mysql to mongo!");
        return MongoProducts.builder()
                .id(products.getId())
                .productName(products.getProductName())
                .quantity(products.getQuantity())
                .price(products.getPrice())
                .build();
    }
}

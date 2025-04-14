package com.arun.spring_batch.config;

import com.arun.spring_batch.model.Products;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class ProductsProcessor implements ItemProcessor<Products, Products> {


    @Override
    public Products process(Products products) throws Exception {
//        log.info("Processing data from CSV to MySQL, product name --> {}", products.getProductName());
        log.info("--------------Data processing from csv to mysql-----------");
        return products;
    }
}

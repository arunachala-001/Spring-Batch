package com.arun.spring_batch.repository;

import com.arun.spring_batch.mongo.model.MongoProducts;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoProductsRepository extends MongoRepository<MongoProducts, Long> {
}

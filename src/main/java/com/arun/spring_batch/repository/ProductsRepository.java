package com.arun.spring_batch.repository;

import com.arun.spring_batch.model.Products;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductsRepository extends JpaRepository<Products, Long> {
}

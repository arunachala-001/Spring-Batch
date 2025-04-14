# 🛠 Spring Batch: CSV → MySQL → MongoDB Data Migration

This Spring Boot 3.4.4 application uses **Spring Batch** to migrate large datasets through a multi-step process:

1. **Step 1:** Read data from a large CSV file in `src/main/resources`
2. **Step 2:** Write the CSV data to a **MySQL** database
3. **Step 3:** Read from MySQL and write into a **MongoDB** collection

Built with Java 17 and Spring Boot 3.4.4

---

## 📦 Technologies Used

- Java 17
- Spring Boot 3.4.4
- Spring Batch
- Spring Data JPA (MySQL)
- Spring Data MongoDB
- Maven

---

## 🔄 Batch Flow Overview

resources/products.csv
        ↓
[Step 1] FlatFileItemReader → MySQL RepositoryItemWriter
        ↓
[Step 2] RepositoryItemReader (MySQL) → ItemProcessor (MySQL Entity → MongoDB Document) → MongoRepositoryItemWriter (MongoDB)

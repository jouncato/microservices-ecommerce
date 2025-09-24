package com.onlineboutique.catalog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Product Catalog Service Application.
 * Migrated from: src/productcatalogservice/server.go
 */
@SpringBootApplication
@EnableJpaRepositories
public class CatalogApplication {

    public static void main(String[] args) {
        SpringApplication.run(CatalogApplication.class, args);
    }
}

package com.onlineboutique.currency;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Currency Service Application.
 * Migrated from: src/currencyservice/server.js
 */
@SpringBootApplication
@EnableJpaRepositories
public class CurrencyApplication {

    public static void main(String[] args) {
        SpringApplication.run(CurrencyApplication.class, args);
    }
}

package com.onlineboutique.checkout;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Checkout Service Application.
 * Migrated from: src/checkoutservice/main.go
 */
@SpringBootApplication
@EnableJpaRepositories
public class CheckoutApplication {

    public static void main(String[] args) {
        SpringApplication.run(CheckoutApplication.class, args);
    }
}

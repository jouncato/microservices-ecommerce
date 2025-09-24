package com.onlineboutique.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Cart Service Application.
 * Migrated from: src/cartservice/src/Program.cs
 */
@SpringBootApplication
@EnableJpaRepositories
public class CartApplication {

    public static void main(String[] args) {
        SpringApplication.run(CartApplication.class, args);
    }
}

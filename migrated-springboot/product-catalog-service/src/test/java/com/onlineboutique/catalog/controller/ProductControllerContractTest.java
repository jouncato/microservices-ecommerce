package com.onlineboutique.catalog.controller;

import com.onlineboutique.catalog.entity.ProductEntity;
import com.onlineboutique.catalog.repository.ProductRepository;
import com.onlineboutique.common.model.Money;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Contract tests for ProductController REST endpoints.
 * Migrated from: Original gRPC service contracts
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class ProductControllerContractTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        // Setup test data
        ProductEntity product = new ProductEntity();
        product.setId("CONTRACT123");
        product.setName("Contract Test Product");
        product.setDescription("A product for contract testing");
        product.setPicture("/contract-test.jpg");
        product.setPriceUsdUnits(29L);
        product.setPriceUsdNanos(990000000);
        product.setCategories(Arrays.asList("contract", "test", "api"));
        
        productRepository.save(product);
    }

    @Test
    void testListProductsContract() throws Exception {
        mockMvc.perform(get("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].description").exists())
                .andExpect(jsonPath("$[0].picture").exists())
                .andExpect(jsonPath("$[0].priceUsd").exists())
                .andExpect(jsonPath("$[0].priceUsd.currencyCode").value("USD"))
                .andExpect(jsonPath("$[0].priceUsd.units").exists())
                .andExpect(jsonPath("$[0].priceUsd.nanos").exists())
                .andExpect(jsonPath("$[0].categories").isArray());
    }

    @Test
    void testGetProductContract() throws Exception {
        mockMvc.perform(get("/api/v1/products/CONTRACT123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("CONTRACT123"))
                .andExpect(jsonPath("$.name").value("Contract Test Product"))
                .andExpect(jsonPath("$.description").value("A product for contract testing"))
                .andExpect(jsonPath("$.picture").value("/contract-test.jpg"))
                .andExpect(jsonPath("$.priceUsd.currencyCode").value("USD"))
                .andExpect(jsonPath("$.priceUsd.units").value(29))
                .andExpect(jsonPath("$.priceUsd.nanos").value(990000000))
                .andExpect(jsonPath("$.categories").isArray())
                .andExpect(jsonPath("$.categories[0]").value("contract"));
    }

    @Test
    void testGetProductNotFoundContract() throws Exception {
        mockMvc.perform(get("/api/v1/products/NOTFOUND")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testSearchProductsContract() throws Exception {
        mockMvc.perform(get("/api/v1/products/search")
                .param("query", "contract")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value("CONTRACT123"))
                .andExpect(jsonPath("$[0].name").value("Contract Test Product"));
    }

    @Test
    void testGetProductsByCategoryContract() throws Exception {
        mockMvc.perform(get("/api/v1/products/category/contract")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value("CONTRACT123"))
                .andExpect(jsonPath("$[0].categories").isArray());
    }

    @Test
    void testGetProductsByCategoriesContract() throws Exception {
        mockMvc.perform(get("/api/v1/products/categories")
                .param("categories", "contract", "test")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value("CONTRACT123"));
    }

    @Test
    void testGetProductsByPriceRangeContract() throws Exception {
        mockMvc.perform(get("/api/v1/products/price-range")
                .param("minPrice", "2500000000") // $25.00 in nanos
                .param("maxPrice", "3500000000") // $35.00 in nanos
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value("CONTRACT123"));
    }

    @Test
    void testHealthCheckContract() throws Exception {
        mockMvc.perform(get("/api/v1/products/health")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Product Catalog Service is healthy"));
    }
}

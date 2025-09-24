package com.onlineboutique.catalog.integration;

import com.onlineboutique.catalog.entity.ProductEntity;
import com.onlineboutique.catalog.repository.ProductRepository;
import com.onlineboutique.common.model.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for ProductRepository with Testcontainers.
 * Migrated from: src/productcatalogservice/product_catalog_test.go integration tests
 */
@DataJpaTest
@ActiveProfiles("test")
class ProductRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    private ProductEntity testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new ProductEntity();
        testProduct.setId("TEST123");
        testProduct.setName("Test Product");
        testProduct.setDescription("A test product for integration testing");
        testProduct.setPicture("/test.jpg");
        testProduct.setPriceUsdUnits(19L);
        testProduct.setPriceUsdNanos(990000000);
        testProduct.setCategories(Arrays.asList("test", "category", "integration"));
    }

    @Test
    void testSaveAndFindProduct() {
        // Given
        entityManager.persistAndFlush(testProduct);

        // When
        Optional<ProductEntity> found = productRepository.findById("TEST123");

        // Then
        assertTrue(found.isPresent());
        assertEquals("TEST123", found.get().getId());
        assertEquals("Test Product", found.get().getName());
        assertEquals("A test product for integration testing", found.get().getDescription());
        assertEquals("/test.jpg", found.get().getPicture());
        assertEquals(19L, found.get().getPriceUsdUnits());
        assertEquals(990000000, found.get().getPriceUsdNanos());
        assertEquals(Arrays.asList("test", "category", "integration"), found.get().getCategories());
    }

    @Test
    void testFindByCategory() {
        // Given
        entityManager.persistAndFlush(testProduct);

        // When
        List<ProductEntity> products = productRepository.findByCategory("test");

        // Then
        assertNotNull(products);
        assertEquals(1, products.size());
        assertEquals("TEST123", products.get(0).getId());
    }

    @Test
    void testSearchProducts() {
        // Given
        entityManager.persistAndFlush(testProduct);

        // When
        List<ProductEntity> products = productRepository.searchProducts("test");

        // Then
        assertNotNull(products);
        assertEquals(1, products.size());
        assertEquals("TEST123", products.get(0).getId());
    }

    @Test
    void testSearchProductsByDescription() {
        // Given
        entityManager.persistAndFlush(testProduct);

        // When
        List<ProductEntity> products = productRepository.searchProducts("integration");

        // Then
        assertNotNull(products);
        assertEquals(1, products.size());
        assertEquals("TEST123", products.get(0).getId());
    }

    @Test
    void testFindByCategories() {
        // Given
        entityManager.persistAndFlush(testProduct);

        // When
        List<ProductEntity> products = productRepository.findByCategories(Arrays.asList("test", "category"));

        // Then
        assertNotNull(products);
        assertEquals(1, products.size());
        assertEquals("TEST123", products.get(0).getId());
    }

    @Test
    void testFindByPriceRange() {
        // Given
        entityManager.persistAndFlush(testProduct);

        // When - search for products between $15 and $25
        Long minPrice = 1500000000L; // $15.00 in nanos
        Long maxPrice = 2500000000L; // $25.00 in nanos
        List<ProductEntity> products = productRepository.findByPriceRange(minPrice, maxPrice);

        // Then
        assertNotNull(products);
        assertEquals(1, products.size());
        assertEquals("TEST123", products.get(0).getId());
    }

    @Test
    void testFindByPriceRangeNoMatches() {
        // Given
        entityManager.persistAndFlush(testProduct);

        // When - search for products between $5 and $10
        Long minPrice = 500000000L; // $5.00 in nanos
        Long maxPrice = 1000000000L; // $10.00 in nanos
        List<ProductEntity> products = productRepository.findByPriceRange(minPrice, maxPrice);

        // Then
        assertNotNull(products);
        assertEquals(0, products.size());
    }

    @Test
    void testFindAllProducts() {
        // Given
        ProductEntity product2 = new ProductEntity();
        product2.setId("TEST456");
        product2.setName("Another Product");
        product2.setDescription("Another test product");
        product2.setPicture("/test2.jpg");
        product2.setPriceUsdUnits(25L);
        product2.setPriceUsdNanos(500000000);
        product2.setCategories(Arrays.asList("another", "category"));

        entityManager.persistAndFlush(testProduct);
        entityManager.persistAndFlush(product2);

        // When
        List<ProductEntity> products = productRepository.findAll();

        // Then
        assertNotNull(products);
        assertEquals(2, products.size());
        assertTrue(products.stream().anyMatch(p -> "TEST123".equals(p.getId())));
        assertTrue(products.stream().anyMatch(p -> "TEST456".equals(p.getId())));
    }

    @Test
    void testProductMoneyConversion() {
        // Given
        entityManager.persistAndFlush(testProduct);

        // When
        Optional<ProductEntity> found = productRepository.findById("TEST123");

        // Then
        assertTrue(found.isPresent());
        Money price = found.get().getPriceUsd();
        assertEquals("USD", price.getCurrencyCode());
        assertEquals(19L, price.getUnits());
        assertEquals(990000000, price.getNanos());
    }
}

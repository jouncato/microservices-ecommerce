package com.onlineboutique.catalog.service;

import com.onlineboutique.catalog.entity.ProductEntity;
import com.onlineboutique.catalog.repository.ProductRepository;
import com.onlineboutique.common.model.Money;
import com.onlineboutique.common.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ProductService.
 * Migrated from: src/productcatalogservice/product_catalog_test.go
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private ProductEntity testProductEntity;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProductEntity = new ProductEntity();
        testProductEntity.setId("TEST123");
        testProductEntity.setName("Test Product");
        testProductEntity.setDescription("A test product");
        testProductEntity.setPicture("/test.jpg");
        testProductEntity.setPriceUsdUnits(19L);
        testProductEntity.setPriceUsdNanos(990000000);
        testProductEntity.setCategories(Arrays.asList("test", "category"));

        testProduct = new Product();
        testProduct.setId("TEST123");
        testProduct.setName("Test Product");
        testProduct.setDescription("A test product");
        testProduct.setPicture("/test.jpg");
        testProduct.setPriceUsd(new Money("USD", 19L, 990000000));
        testProduct.setCategories(new String[]{"test", "category"});
    }

    @Test
    void testListProducts() {
        // Given
        List<ProductEntity> entities = Arrays.asList(testProductEntity);
        when(productRepository.findAll()).thenReturn(entities);

        // When
        List<Product> products = productService.listProducts();

        // Then
        assertNotNull(products);
        assertEquals(1, products.size());
        assertEquals("TEST123", products.get(0).getId());
        assertEquals("Test Product", products.get(0).getName());
        verify(productRepository).findAll();
    }

    @Test
    void testGetProduct() {
        // Given
        when(productRepository.findById("TEST123")).thenReturn(Optional.of(testProductEntity));

        // When
        Optional<Product> product = productService.getProduct("TEST123");

        // Then
        assertTrue(product.isPresent());
        assertEquals("TEST123", product.get().getId());
        assertEquals("Test Product", product.get().getName());
        verify(productRepository).findById("TEST123");
    }

    @Test
    void testGetProductNotFound() {
        // Given
        when(productRepository.findById("NOTFOUND")).thenReturn(Optional.empty());

        // When
        Optional<Product> product = productService.getProduct("NOTFOUND");

        // Then
        assertFalse(product.isPresent());
        verify(productRepository).findById("NOTFOUND");
    }

    @Test
    void testSearchProducts() {
        // Given
        List<ProductEntity> entities = Arrays.asList(testProductEntity);
        when(productRepository.searchProducts("test")).thenReturn(entities);

        // When
        List<Product> products = productService.searchProducts("test");

        // Then
        assertNotNull(products);
        assertEquals(1, products.size());
        assertEquals("TEST123", products.get(0).getId());
        verify(productRepository).searchProducts("test");
    }

    @Test
    void testGetProductsByCategory() {
        // Given
        List<ProductEntity> entities = Arrays.asList(testProductEntity);
        when(productRepository.findByCategory("test")).thenReturn(entities);

        // When
        List<Product> products = productService.getProductsByCategory("test");

        // Then
        assertNotNull(products);
        assertEquals(1, products.size());
        assertEquals("TEST123", products.get(0).getId());
        verify(productRepository).findByCategory("test");
    }

    @Test
    void testGetProductsByCategories() {
        // Given
        List<String> categories = Arrays.asList("test", "category");
        List<ProductEntity> entities = Arrays.asList(testProductEntity);
        when(productRepository.findByCategories(categories)).thenReturn(entities);

        // When
        List<Product> products = productService.getProductsByCategories(categories);

        // Then
        assertNotNull(products);
        assertEquals(1, products.size());
        assertEquals("TEST123", products.get(0).getId());
        verify(productRepository).findByCategories(categories);
    }

    @Test
    void testGetProductsByPriceRange() {
        // Given
        Long minPrice = 1000000000L; // $1.00
        Long maxPrice = 2000000000L; // $2.00
        List<ProductEntity> entities = Arrays.asList(testProductEntity);
        when(productRepository.findByPriceRange(minPrice, maxPrice)).thenReturn(entities);

        // When
        List<Product> products = productService.getProductsByPriceRange(minPrice, maxPrice);

        // Then
        assertNotNull(products);
        assertEquals(1, products.size());
        assertEquals("TEST123", products.get(0).getId());
        verify(productRepository).findByPriceRange(minPrice, maxPrice);
    }
}

package com.onlineboutique.catalog.service;

import com.onlineboutique.catalog.entity.ProductEntity;
import com.onlineboutique.catalog.repository.ProductRepository;
import com.onlineboutique.common.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Product service for business logic.
 * Migrated from: src/productcatalogservice/product_catalog.go business logic
 */
@Service
@Transactional
public class ProductService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    
    @Autowired
    private ProductRepository productRepository;
    
    /**
     * List all products
     * Migrated from: ListProducts gRPC method
     */
    public List<Product> listProducts() {
        logger.debug("Listing all products");
        List<ProductEntity> entities = productRepository.findAll();
        return entities.stream()
                .map(this::convertToProduct)
                .collect(Collectors.toList());
    }
    
    /**
     * Get product by ID
     * Migrated from: GetProduct gRPC method
     */
    public Optional<Product> getProduct(String productId) {
        logger.debug("Getting product with ID: {}", productId);
        return productRepository.findById(productId)
                .map(this::convertToProduct);
    }
    
    /**
     * Search products by query
     * Migrated from: SearchProducts gRPC method
     */
    public List<Product> searchProducts(String query) {
        logger.debug("Searching products with query: {}", query);
        List<ProductEntity> entities = productRepository.searchProducts(query);
        return entities.stream()
                .map(this::convertToProduct)
                .collect(Collectors.toList());
    }
    
    /**
     * Get products by category
     */
    public List<Product> getProductsByCategory(String category) {
        logger.debug("Getting products by category: {}", category);
        List<ProductEntity> entities = productRepository.findByCategory(category);
        return entities.stream()
                .map(this::convertToProduct)
                .collect(Collectors.toList());
    }
    
    /**
     * Get products by multiple categories
     */
    public List<Product> getProductsByCategories(List<String> categories) {
        logger.debug("Getting products by categories: {}", categories);
        List<ProductEntity> entities = productRepository.findByCategories(categories);
        return entities.stream()
                .map(this::convertToProduct)
                .collect(Collectors.toList());
    }
    
    /**
     * Get products by price range
     */
    public List<Product> getProductsByPriceRange(Long minPriceNanos, Long maxPriceNanos) {
        logger.debug("Getting products by price range: {} - {}", minPriceNanos, maxPriceNanos);
        List<ProductEntity> entities = productRepository.findByPriceRange(minPriceNanos, maxPriceNanos);
        return entities.stream()
                .map(this::convertToProduct)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert ProductEntity to Product model
     */
    private Product convertToProduct(ProductEntity entity) {
        Product product = new Product();
        product.setId(entity.getId());
        product.setName(entity.getName());
        product.setDescription(entity.getDescription());
        product.setPicture(entity.getPicture());
        product.setPriceUsd(entity.getPriceUsd());
        product.setCategories(entity.getCategories().toArray(new String[0]));
        return product;
    }
    
    /**
     * Convert Product model to ProductEntity
     */
    private ProductEntity convertToEntity(Product product) {
        ProductEntity entity = new ProductEntity();
        entity.setId(product.getId());
        entity.setName(product.getName());
        entity.setDescription(product.getDescription());
        entity.setPicture(product.getPicture());
        entity.setPriceUsd(product.getPriceUsd());
        entity.setCategories(List.of(product.getCategories()));
        return entity;
    }
}

package com.onlineboutique.catalog.controller;

import com.onlineboutique.catalog.service.ProductService;
import com.onlineboutique.common.model.Product;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Product catalog REST controller.
 * Migrated from: src/productcatalogservice/server.go gRPC endpoints
 */
@RestController
@RequestMapping("/api/v1/products")
@Timed("product_catalog_service")
public class ProductController {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    
    @Autowired
    private ProductService productService;
    
    /**
     * List all products
     * Migrated from: ListProducts gRPC method
     */
    @GetMapping
    public ResponseEntity<List<Product>> listProducts() {
        logger.info("Listing all products");
        List<Product> products = productService.listProducts();
        return ResponseEntity.ok(products);
    }
    
    /**
     * Get product by ID
     * Migrated from: GetProduct gRPC method
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable String id) {
        logger.info("Getting product with ID: {}", id);
        Optional<Product> product = productService.getProduct(id);
        return product.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Search products by query
     * Migrated from: SearchProducts gRPC method
     */
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String query) {
        logger.info("Searching products with query: {}", query);
        List<Product> products = productService.searchProducts(query);
        return ResponseEntity.ok(products);
    }
    
    /**
     * Get products by category
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String category) {
        logger.info("Getting products by category: {}", category);
        List<Product> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(products);
    }
    
    /**
     * Get products by multiple categories
     */
    @GetMapping("/categories")
    public ResponseEntity<List<Product>> getProductsByCategories(@RequestParam List<String> categories) {
        logger.info("Getting products by categories: {}", categories);
        List<Product> products = productService.getProductsByCategories(categories);
        return ResponseEntity.ok(products);
    }
    
    /**
     * Get products by price range
     */
    @GetMapping("/price-range")
    public ResponseEntity<List<Product>> getProductsByPriceRange(
            @RequestParam Long minPrice, 
            @RequestParam Long maxPrice) {
        logger.info("Getting products by price range: {} - {}", minPrice, maxPrice);
        List<Product> products = productService.getProductsByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(products);
    }
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Product Catalog Service is healthy");
    }
}

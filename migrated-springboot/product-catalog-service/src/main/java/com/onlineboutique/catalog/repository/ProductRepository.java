package com.onlineboutique.catalog.repository;

import com.onlineboutique.catalog.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Product repository for database operations.
 * Migrated from: src/productcatalogservice/product_catalog.go database operations
 */
@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, String> {
    
    /**
     * Find products by category
     */
    @Query("SELECT p FROM ProductEntity p WHERE :category MEMBER OF p.categories")
    List<ProductEntity> findByCategory(@Param("category") String category);
    
    /**
     * Search products by name or description
     */
    @Query("SELECT p FROM ProductEntity p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<ProductEntity> searchProducts(@Param("query") String query);
    
    /**
     * Find products by multiple categories
     */
    @Query("SELECT p FROM ProductEntity p WHERE p.categories && :categories")
    List<ProductEntity> findByCategories(@Param("categories") List<String> categories);
    
    /**
     * Find products by price range
     */
    @Query("SELECT p FROM ProductEntity p WHERE " +
           "(p.priceUsdUnits * 1000000000 + p.priceUsdNanos) BETWEEN :minPrice AND :maxPrice")
    List<ProductEntity> findByPriceRange(@Param("minPrice") Long minPrice, @Param("maxPrice") Long maxPrice);
}

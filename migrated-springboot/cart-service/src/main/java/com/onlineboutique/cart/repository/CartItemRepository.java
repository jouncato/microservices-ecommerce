package com.onlineboutique.cart.repository;

import com.onlineboutique.cart.entity.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Cart item repository for database operations.
 * Migrated from: src/cartservice/src/cartstore/ICartStore.cs interface
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {
    
    /**
     * Find all cart items for a user
     */
    List<CartItemEntity> findByUserIdOrderByCreatedAtAsc(String userId);
    
    /**
     * Find specific cart item for a user and product
     */
    Optional<CartItemEntity> findByUserIdAndProductId(String userId, String productId);
    
    /**
     * Delete all cart items for a user
     */
    @Modifying
    @Query("DELETE FROM CartItemEntity c WHERE c.userId = :userId")
    void deleteByUserId(@Param("userId") String userId);
    
    /**
     * Check if cart item exists for user and product
     */
    boolean existsByUserIdAndProductId(String userId, String productId);
    
    /**
     * Count total items in cart for a user
     */
    @Query("SELECT COALESCE(SUM(c.quantity), 0) FROM CartItemEntity c WHERE c.userId = :userId")
    Integer countTotalItemsByUserId(@Param("userId") String userId);
}

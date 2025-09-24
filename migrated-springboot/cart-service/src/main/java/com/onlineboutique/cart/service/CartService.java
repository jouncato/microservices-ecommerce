package com.onlineboutique.cart.service;

import com.onlineboutique.cart.entity.CartItemEntity;
import com.onlineboutique.cart.repository.CartItemRepository;
import com.onlineboutique.common.model.CartItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Cart service for business logic.
 * Migrated from: src/cartservice/src/services/CartService.cs
 */
@Service
@Transactional
public class CartService {
    
    private static final Logger logger = LoggerFactory.getLogger(CartService.class);
    
    @Autowired
    private CartItemRepository cartItemRepository;
    
    /**
     * Add item to cart
     * Migrated from: AddItem gRPC method
     */
    public void addItem(String userId, CartItem item) {
        logger.debug("Adding item {} with quantity {} to cart for user {}", 
                   item.getProductId(), item.getQuantity(), userId);
        
        Optional<CartItemEntity> existingItem = cartItemRepository.findByUserIdAndProductId(userId, item.getProductId());
        
        if (existingItem.isPresent()) {
            // Update existing item quantity
            CartItemEntity entity = existingItem.get();
            entity.setQuantity(entity.getQuantity() + item.getQuantity());
            cartItemRepository.save(entity);
            logger.debug("Updated existing cart item for user {} and product {}", userId, item.getProductId());
        } else {
            // Create new cart item
            CartItemEntity entity = new CartItemEntity(userId, item.getProductId(), item.getQuantity());
            cartItemRepository.save(entity);
            logger.debug("Created new cart item for user {} and product {}", userId, item.getProductId());
        }
    }
    
    /**
     * Get cart for user
     * Migrated from: GetCart gRPC method
     */
    public List<CartItem> getCart(String userId) {
        logger.debug("Getting cart for user {}", userId);
        List<CartItemEntity> entities = cartItemRepository.findByUserIdOrderByCreatedAtAsc(userId);
        return entities.stream()
                .map(this::convertToCartItem)
                .collect(Collectors.toList());
    }
    
    /**
     * Empty cart for user
     * Migrated from: EmptyCart gRPC method
     */
    public void emptyCart(String userId) {
        logger.debug("Emptying cart for user {}", userId);
        cartItemRepository.deleteByUserId(userId);
    }
    
    /**
     * Update item quantity in cart
     */
    public void updateItemQuantity(String userId, String productId, Integer quantity) {
        logger.debug("Updating quantity for product {} to {} for user {}", productId, quantity, userId);
        
        Optional<CartItemEntity> existingItem = cartItemRepository.findByUserIdAndProductId(userId, productId);
        
        if (existingItem.isPresent()) {
            if (quantity <= 0) {
                cartItemRepository.delete(existingItem.get());
                logger.debug("Removed item {} from cart for user {}", productId, userId);
            } else {
                CartItemEntity entity = existingItem.get();
                entity.setQuantity(quantity);
                cartItemRepository.save(entity);
                logger.debug("Updated quantity for item {} to {} for user {}", productId, quantity, userId);
            }
        } else if (quantity > 0) {
            CartItemEntity entity = new CartItemEntity(userId, productId, quantity);
            cartItemRepository.save(entity);
            logger.debug("Added new item {} with quantity {} for user {}", productId, quantity, userId);
        }
    }
    
    /**
     * Remove specific item from cart
     */
    public void removeItem(String userId, String productId) {
        logger.debug("Removing item {} from cart for user {}", productId, userId);
        cartItemRepository.findByUserIdAndProductId(userId, productId)
                .ifPresent(cartItemRepository::delete);
    }
    
    /**
     * Get total item count in cart
     */
    public Integer getTotalItemCount(String userId) {
        logger.debug("Getting total item count for user {}", userId);
        return cartItemRepository.countTotalItemsByUserId(userId);
    }
    
    /**
     * Check if cart is empty
     */
    public boolean isCartEmpty(String userId) {
        return getTotalItemCount(userId) == 0;
    }
    
    /**
     * Convert CartItemEntity to CartItem model
     */
    private CartItem convertToCartItem(CartItemEntity entity) {
        return new CartItem(entity.getProductId(), entity.getQuantity());
    }
    
    /**
     * Convert CartItem model to CartItemEntity
     */
    private CartItemEntity convertToEntity(String userId, CartItem item) {
        return new CartItemEntity(userId, item.getProductId(), item.getQuantity());
    }
}

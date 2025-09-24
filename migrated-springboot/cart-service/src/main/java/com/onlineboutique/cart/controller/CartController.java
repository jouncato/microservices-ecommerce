package com.onlineboutique.cart.controller;

import com.onlineboutique.cart.service.CartService;
import com.onlineboutique.common.model.CartItem;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Cart REST controller.
 * Migrated from: src/cartservice/src/services/CartService.cs gRPC endpoints
 */
@RestController
@RequestMapping("/api/v1/cart")
@Timed("cart_service")
public class CartController {
    
    private static final Logger logger = LoggerFactory.getLogger(CartController.class);
    
    @Autowired
    private CartService cartService;
    
    /**
     * Add item to cart
     * Migrated from: AddItem gRPC method
     */
    @PostMapping("/{userId}/items")
    public ResponseEntity<Void> addItem(@PathVariable String userId, @RequestBody CartItem item) {
        logger.info("Adding item {} with quantity {} to cart for user {}", 
                   item.getProductId(), item.getQuantity(), userId);
        cartService.addItem(userId, item);
        return ResponseEntity.ok().build();
    }
    
    /**
     * Get cart for user
     * Migrated from: GetCart gRPC method
     */
    @GetMapping("/{userId}")
    public ResponseEntity<List<CartItem>> getCart(@PathVariable String userId) {
        logger.info("Getting cart for user {}", userId);
        List<CartItem> cart = cartService.getCart(userId);
        return ResponseEntity.ok(cart);
    }
    
    /**
     * Empty cart for user
     * Migrated from: EmptyCart gRPC method
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> emptyCart(@PathVariable String userId) {
        logger.info("Emptying cart for user {}", userId);
        cartService.emptyCart(userId);
        return ResponseEntity.ok().build();
    }
    
    /**
     * Update item quantity in cart
     */
    @PutMapping("/{userId}/items/{productId}")
    public ResponseEntity<Void> updateItemQuantity(
            @PathVariable String userId, 
            @PathVariable String productId, 
            @RequestParam Integer quantity) {
        logger.info("Updating quantity for product {} to {} for user {}", productId, quantity, userId);
        cartService.updateItemQuantity(userId, productId, quantity);
        return ResponseEntity.ok().build();
    }
    
    /**
     * Remove specific item from cart
     */
    @DeleteMapping("/{userId}/items/{productId}")
    public ResponseEntity<Void> removeItem(@PathVariable String userId, @PathVariable String productId) {
        logger.info("Removing item {} from cart for user {}", productId, userId);
        cartService.removeItem(userId, productId);
        return ResponseEntity.ok().build();
    }
    
    /**
     * Get total item count in cart
     */
    @GetMapping("/{userId}/count")
    public ResponseEntity<Integer> getTotalItemCount(@PathVariable String userId) {
        logger.info("Getting total item count for user {}", userId);
        Integer count = cartService.getTotalItemCount(userId);
        return ResponseEntity.ok(count);
    }
    
    /**
     * Check if cart is empty
     */
    @GetMapping("/{userId}/empty")
    public ResponseEntity<Boolean> isCartEmpty(@PathVariable String userId) {
        logger.info("Checking if cart is empty for user {}", userId);
        boolean isEmpty = cartService.isCartEmpty(userId);
        return ResponseEntity.ok(isEmpty);
    }
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Cart Service is healthy");
    }
}

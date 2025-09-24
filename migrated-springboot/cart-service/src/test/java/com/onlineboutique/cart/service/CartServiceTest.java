package com.onlineboutique.cart.service;

import com.onlineboutique.cart.entity.CartItemEntity;
import com.onlineboutique.cart.repository.CartItemRepository;
import com.onlineboutique.common.model.CartItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CartService.
 * Migrated from: src/cartservice/tests/CartServiceTests.cs
 */
@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private CartService cartService;

    private CartItem testCartItem;
    private CartItemEntity testCartItemEntity;
    private String testUserId = "test-user-123";

    @BeforeEach
    void setUp() {
        testCartItem = new CartItem("PROD123", 2);
        
        testCartItemEntity = new CartItemEntity();
        testCartItemEntity.setId(1L);
        testCartItemEntity.setUserId(testUserId);
        testCartItemEntity.setProductId("PROD123");
        testCartItemEntity.setQuantity(2);
        testCartItemEntity.setCreatedAt(LocalDateTime.now());
        testCartItemEntity.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testAddItemNewItem() {
        // Given
        when(cartItemRepository.findByUserIdAndProductId(testUserId, "PROD123"))
                .thenReturn(Optional.empty());
        when(cartItemRepository.save(any(CartItemEntity.class)))
                .thenReturn(testCartItemEntity);

        // When
        cartService.addItem(testUserId, testCartItem);

        // Then
        verify(cartItemRepository).findByUserIdAndProductId(testUserId, "PROD123");
        verify(cartItemRepository).save(any(CartItemEntity.class));
    }

    @Test
    void testAddItemExistingItem() {
        // Given
        CartItemEntity existingEntity = new CartItemEntity();
        existingEntity.setUserId(testUserId);
        existingEntity.setProductId("PROD123");
        existingEntity.setQuantity(1);
        
        when(cartItemRepository.findByUserIdAndProductId(testUserId, "PROD123"))
                .thenReturn(Optional.of(existingEntity));
        when(cartItemRepository.save(any(CartItemEntity.class)))
                .thenReturn(existingEntity);

        // When
        cartService.addItem(testUserId, testCartItem);

        // Then
        verify(cartItemRepository).findByUserIdAndProductId(testUserId, "PROD123");
        verify(cartItemRepository).save(existingEntity);
        assertEquals(3, existingEntity.getQuantity()); // 1 + 2
    }

    @Test
    void testGetCart() {
        // Given
        List<CartItemEntity> entities = Arrays.asList(testCartItemEntity);
        when(cartItemRepository.findByUserIdOrderByCreatedAtAsc(testUserId))
                .thenReturn(entities);

        // When
        List<CartItem> cart = cartService.getCart(testUserId);

        // Then
        assertNotNull(cart);
        assertEquals(1, cart.size());
        assertEquals("PROD123", cart.get(0).getProductId());
        assertEquals(2, cart.get(0).getQuantity());
        verify(cartItemRepository).findByUserIdOrderByCreatedAtAsc(testUserId);
    }

    @Test
    void testEmptyCart() {
        // Given
        doNothing().when(cartItemRepository).deleteByUserId(testUserId);

        // When
        cartService.emptyCart(testUserId);

        // Then
        verify(cartItemRepository).deleteByUserId(testUserId);
    }

    @Test
    void testUpdateItemQuantityIncrease() {
        // Given
        when(cartItemRepository.findByUserIdAndProductId(testUserId, "PROD123"))
                .thenReturn(Optional.of(testCartItemEntity));
        when(cartItemRepository.save(any(CartItemEntity.class)))
                .thenReturn(testCartItemEntity);

        // When
        cartService.updateItemQuantity(testUserId, "PROD123", 5);

        // Then
        verify(cartItemRepository).findByUserIdAndProductId(testUserId, "PROD123");
        verify(cartItemRepository).save(testCartItemEntity);
        assertEquals(5, testCartItemEntity.getQuantity());
    }

    @Test
    void testUpdateItemQuantityToZero() {
        // Given
        when(cartItemRepository.findByUserIdAndProductId(testUserId, "PROD123"))
                .thenReturn(Optional.of(testCartItemEntity));

        // When
        cartService.updateItemQuantity(testUserId, "PROD123", 0);

        // Then
        verify(cartItemRepository).findByUserIdAndProductId(testUserId, "PROD123");
        verify(cartItemRepository).delete(testCartItemEntity);
    }

    @Test
    void testUpdateItemQuantityNewItem() {
        // Given
        when(cartItemRepository.findByUserIdAndProductId(testUserId, "PROD456"))
                .thenReturn(Optional.empty());
        when(cartItemRepository.save(any(CartItemEntity.class)))
                .thenReturn(new CartItemEntity());

        // When
        cartService.updateItemQuantity(testUserId, "PROD456", 3);

        // Then
        verify(cartItemRepository).findByUserIdAndProductId(testUserId, "PROD456");
        verify(cartItemRepository).save(any(CartItemEntity.class));
    }

    @Test
    void testRemoveItem() {
        // Given
        when(cartItemRepository.findByUserIdAndProductId(testUserId, "PROD123"))
                .thenReturn(Optional.of(testCartItemEntity));

        // When
        cartService.removeItem(testUserId, "PROD123");

        // Then
        verify(cartItemRepository).findByUserIdAndProductId(testUserId, "PROD123");
        verify(cartItemRepository).delete(testCartItemEntity);
    }

    @Test
    void testRemoveItemNotFound() {
        // Given
        when(cartItemRepository.findByUserIdAndProductId(testUserId, "NOTFOUND"))
                .thenReturn(Optional.empty());

        // When
        cartService.removeItem(testUserId, "NOTFOUND");

        // Then
        verify(cartItemRepository).findByUserIdAndProductId(testUserId, "NOTFOUND");
        verify(cartItemRepository, never()).delete(any());
    }

    @Test
    void testGetTotalItemCount() {
        // Given
        when(cartItemRepository.countTotalItemsByUserId(testUserId)).thenReturn(5);

        // When
        Integer count = cartService.getTotalItemCount(testUserId);

        // Then
        assertEquals(5, count);
        verify(cartItemRepository).countTotalItemsByUserId(testUserId);
    }

    @Test
    void testIsCartEmpty() {
        // Given
        when(cartItemRepository.countTotalItemsByUserId(testUserId)).thenReturn(0);

        // When
        boolean isEmpty = cartService.isCartEmpty(testUserId);

        // Then
        assertTrue(isEmpty);
        verify(cartItemRepository).countTotalItemsByUserId(testUserId);
    }

    @Test
    void testIsCartNotEmpty() {
        // Given
        when(cartItemRepository.countTotalItemsByUserId(testUserId)).thenReturn(3);

        // When
        boolean isEmpty = cartService.isCartEmpty(testUserId);

        // Then
        assertFalse(isEmpty);
        verify(cartItemRepository).countTotalItemsByUserId(testUserId);
    }
}

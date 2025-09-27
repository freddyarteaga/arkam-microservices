package com.arkam.order.application.service;

import com.arkam.order.domain.dto.CartItemRequest;
import com.arkam.order.domain.dto.ProductResponse;
import com.arkam.order.domain.dto.UserResponse;
import com.arkam.order.domain.model.CartItem;
import com.arkam.order.infrastructure.clients.ProductServiceClient;
import com.arkam.order.infrastructure.clients.UserServiceClient;
import com.arkam.order.infrastructure.repository.CartItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductServiceClient productServiceClient;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private CartService cartService;

    private CartItemRequest cartItemRequest;
    private ProductResponse productResponse;
    private UserResponse userResponse;
    private CartItem existingCartItem;
    private String userId;

    @BeforeEach
    void setUp() {
        userId = "USER-001";

        cartItemRequest = new CartItemRequest();
        cartItemRequest.setProductId("PROD-001");
        cartItemRequest.setQuantity(2);

        productResponse = new ProductResponse();
        productResponse.setId(1L);
        productResponse.setName("Test Product");
        productResponse.setPrice(new BigDecimal("10.00"));
        productResponse.setStockQuantity(10);

        userResponse = new UserResponse();
        userResponse.setId("USER-001");
        userResponse.setFirstName("testuser");
        userResponse.setEmail("test@example.com");

        existingCartItem = new CartItem();
        existingCartItem.setId(1L);
        existingCartItem.setUserId(userId);
        existingCartItem.setProductId("PROD-001");
        existingCartItem.setQuantity(1);
        existingCartItem.setPrice(new BigDecimal("1000.00"));
    }

    @Test
    void addToCart_WithValidData_ShouldReturnTrue() {
        // Given
        when(productServiceClient.getProductDetails("PROD-001")).thenReturn(productResponse);
        when(userServiceClient.getUserDetails(userId)).thenReturn(userResponse);
        when(cartItemRepository.findByUserIdAndProductId(userId, "PROD-001")).thenReturn(null);
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(new CartItem());

        // When
        boolean result = cartService.addToCart(userId, cartItemRequest);

        // Then
        assertTrue(result);
        verify(productServiceClient).getProductDetails("PROD-001");
        verify(userServiceClient).getUserDetails(userId);
        verify(cartItemRepository).save(any(CartItem.class));
    }

    @Test
    void addToCart_WithExistingCartItem_ShouldUpdateQuantity() {
        // Given
        when(productServiceClient.getProductDetails("PROD-001")).thenReturn(productResponse);
        when(userServiceClient.getUserDetails(userId)).thenReturn(userResponse);
        when(cartItemRepository.findByUserIdAndProductId(userId, "PROD-001")).thenReturn(existingCartItem);
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(existingCartItem);

        // When
        boolean result = cartService.addToCart(userId, cartItemRequest);

        // Then
        assertTrue(result);
        assertEquals(3, existingCartItem.getQuantity()); // 1 + 2
        verify(cartItemRepository).save(existingCartItem);
    }

    @Test
    void addToCart_WithInsufficientStock_ShouldReturnFalse() {
        // Given
        productResponse.setStockQuantity(1); // Less than requested quantity
        when(productServiceClient.getProductDetails("PROD-001")).thenReturn(productResponse);

        // When
        boolean result = cartService.addToCart(userId, cartItemRequest);

        // Then
        assertFalse(result);
        verify(productServiceClient).getProductDetails("PROD-001");
        verify(userServiceClient, never()).getUserDetails(anyString());
        verify(cartItemRepository, never()).save(any(CartItem.class));
    }

    @Test
    void addToCart_WithNullProduct_ShouldReturnFalse() {
        // Given
        when(productServiceClient.getProductDetails("PROD-001")).thenReturn(null);

        // When
        boolean result = cartService.addToCart(userId, cartItemRequest);

        // Then
        assertFalse(result);
        verify(productServiceClient).getProductDetails("PROD-001");
        verify(userServiceClient, never()).getUserDetails(anyString());
        verify(cartItemRepository, never()).save(any(CartItem.class));
    }

    @Test
    void addToCart_WithNullUser_ShouldReturnFalse() {
        // Given
        when(productServiceClient.getProductDetails("PROD-001")).thenReturn(productResponse);
        when(userServiceClient.getUserDetails(userId)).thenReturn(null);

        // When
        boolean result = cartService.addToCart(userId, cartItemRequest);

        // Then
        assertFalse(result);
        verify(productServiceClient).getProductDetails("PROD-001");
        verify(userServiceClient).getUserDetails(userId);
        verify(cartItemRepository, never()).save(any(CartItem.class));
    }

    @Test
    void deleteItemFromCart_WithExistingItem_ShouldReturnTrue() {
        // Given
        when(cartItemRepository.findByUserIdAndProductId(userId, "PROD-001")).thenReturn(existingCartItem);

        // When
        boolean result = cartService.deleteItemFromCart(userId, "PROD-001");

        // Then
        assertTrue(result);
        verify(cartItemRepository).findByUserIdAndProductId(userId, "PROD-001");
        verify(cartItemRepository).delete(existingCartItem);
    }

    @Test
    void deleteItemFromCart_WithNonExistingItem_ShouldReturnFalse() {
        // Given
        when(cartItemRepository.findByUserIdAndProductId(userId, "PROD-001")).thenReturn(null);

        // When
        boolean result = cartService.deleteItemFromCart(userId, "PROD-001");

        // Then
        assertFalse(result);
        verify(cartItemRepository).findByUserIdAndProductId(userId, "PROD-001");
        verify(cartItemRepository, never()).delete(any(CartItem.class));
    }

    @Test
    void getCart_WithValidUserId_ShouldReturnCartItems() {
        // Given
        List<CartItem> expectedCartItems = Collections.singletonList(existingCartItem);
        when(cartItemRepository.findByUserId(userId)).thenReturn(expectedCartItems);

        // When
        List<CartItem> result = cartService.getCart(userId).getCartItems();

        // Then
        assertEquals(1, result.size());
        assertEquals(existingCartItem, result.getFirst());
        verify(cartItemRepository).findByUserId(userId);
    }

    @Test
    void getCart_WithEmptyCart_ShouldReturnEmptyList() {
        // Given
        when(cartItemRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

        // When
        List<CartItem> result = cartService.getCart(userId).getCartItems();

        // Then
        assertTrue(result.isEmpty());
        verify(cartItemRepository).findByUserId(userId);
    }

    @Test
    void clearCart_WithValidUserId_ShouldDeleteAllItems() {
        // Given
        doNothing().when(cartItemRepository).deleteByUserId(userId);

        // When
        cartService.clearCart(userId);

        // Then
        verify(cartItemRepository).deleteByUserId(userId);
    }

    @Test
    void addToCartFallBack_ShouldReturnFalse() {
        // Given
        Exception exception = new RuntimeException("Service unavailable");

        // When
        boolean result = cartService.addToCartFallBack(userId, cartItemRequest, exception);

        // Then
        assertFalse(result);
    }
}

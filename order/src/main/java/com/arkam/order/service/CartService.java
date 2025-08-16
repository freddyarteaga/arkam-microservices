package com.arkam.order.service;

import com.arkam.order.clients.ProductServiceClient;
import com.arkam.order.clients.UserServiceClient;
import com.arkam.order.dto.CartItemRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import com.arkam.order.dto.ProductResponse;
import com.arkam.order.dto.UserResponse;
import com.arkam.order.model.CartItem;

import com.arkam.order.repository.CartItemRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {
    private final CartItemRepository cartItemRepository;
    private final ProductServiceClient productServiceClient;
    private final UserServiceClient userServiceClient;

    int attempt = 0;

//    @CircuitBreaker(name = "productService", fallbackMethod = "addToCartFallBack")
    @Retry(name = "retryBreaker", fallbackMethod = "addToCartFallBack")
    public boolean addToCart(String userId, CartItemRequest request) {
        System.out.println("ATTEMPT COUNT: " + ++attempt);
        // Mira por producto
        ProductResponse productResponse = productServiceClient.getProductDetails(request.getProductId());
        if (productResponse == null || productResponse.getStockQuantity() < request.getQuantity())
            return false;

        UserResponse userResponse = userServiceClient.getUserDetails(userId);
        if (userResponse == null)
            return false;


        CartItem existingCartItem = cartItemRepository.findByUserIdAndProductId(userId, request.getProductId());
        if (existingCartItem != null) {
            // Actualiza la cantidad
            existingCartItem.setQuantity(existingCartItem.getQuantity() + request.getQuantity());
            existingCartItem.setPrice(BigDecimal.valueOf(1000.00));
            cartItemRepository.save(existingCartItem);
        } else {
            // Crea u nuevo item en el carrito
            CartItem cartItem = new CartItem();
            cartItem.setUserId(userId);
            cartItem.setProductId(request.getProductId());
            cartItem.setQuantity(request.getQuantity());
            cartItem.setPrice(BigDecimal.valueOf(1000.00));
            cartItemRepository.save(cartItem);
        }
        return true;
    }

    public boolean addToCartFallBack(String userId,
                                     CartItemRequest request,
                                     Exception exception) {
        exception.printStackTrace();
        return false;
    }

    public boolean deleteItemFromCart(String userId, String productId) {
        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(userId, productId);

        if (cartItem != null){
            cartItemRepository.delete(cartItem);
            return true;
        }
        return false;
    }

    public List<CartItem> getCart(String userId) {
        return cartItemRepository.findByUserId(userId);
    }

    public void clearCart(String userId) {
        cartItemRepository.deleteByUserId(userId);
    }
}

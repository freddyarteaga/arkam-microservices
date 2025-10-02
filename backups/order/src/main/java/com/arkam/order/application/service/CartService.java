package com.arkam.order.application.service;

import com.arkam.order.domain.dto.CartResponse;
import com.arkam.order.infrastructure.clients.ProductServiceClient;
import com.arkam.order.infrastructure.clients.UserServiceClient;
import com.arkam.order.domain.dto.CartItemRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import com.arkam.order.domain.dto.ProductResponse;
import com.arkam.order.domain.dto.UserResponse;
import com.arkam.order.domain.model.CartItem;

import com.arkam.order.infrastructure.repository.CartItemRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

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
            existingCartItem.setPrice(productResponse.getPrice());
            cartItemRepository.save(existingCartItem);
        } else {
            // Crea u nuevo item en el carrito
            CartItem cartItem = new CartItem();
            cartItem.setUserId(userId);
            cartItem.setProductId(request.getProductId());
            cartItem.setQuantity(request.getQuantity());
            cartItem.setPrice(productResponse.getPrice());
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

    public CartResponse getCart(String userId) {
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        int totalQuantity = cartItems.stream().mapToInt(CartItem::getQuantity).sum();
        BigDecimal totalPrice = cartItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .cartItems(cartItems)
                .totalQuantity(totalQuantity)
                .totalPrice(totalPrice)
                .build();
    }

    public Mono<CartResponse> getCartReactive(String userId) {
        return Mono.fromCallable(() -> getCart(userId));
    }

    public void clearCart(String userId) {
        cartItemRepository.deleteByUserId(userId);
    }
}

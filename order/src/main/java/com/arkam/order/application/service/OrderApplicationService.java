package com.arkam.order.application.service;

import com.arkam.order.application.dto.CartItemRequest;
import com.arkam.order.application.dto.CartItemResponse;
import com.arkam.order.application.dto.OrderItemDTO;
import com.arkam.order.application.dto.OrderResponse;
import com.arkam.order.application.port.in.*;
import com.arkam.order.application.port.out.CartItemRepositoryPort;
import com.arkam.order.application.port.out.OrderRepositoryPort;
import com.arkam.order.application.port.out.ProductServicePort;
import com.arkam.order.application.port.out.UserServicePort;
import com.arkam.order.domain.model.CartItem;
import com.arkam.order.domain.model.Order;
import com.arkam.order.domain.model.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderApplicationService implements AddToCartUseCase, RemoveFromCartUseCase, GetCartUseCase, CreateOrderUseCase {

    private final CartItemRepositoryPort cartItemRepository;
    private final OrderRepositoryPort orderRepository;
    private final ProductServicePort productService;
    private final UserServicePort userService;

    @Override
    public Mono<Boolean> addToCart(String userId, CartItemRequest request) {
        return userService.getUserDetails(userId)
                .flatMap(user -> {
                    // User exists
                    return productService.getProductDetails(request.getProductId())
                            .flatMap(product -> {
                                if (product.getStockQuantity() < request.getQuantity()) {
                                    return Mono.just(false);
                                }
                                return cartItemRepository.findByUserIdAndProductId(userId, request.getProductId())
                                        .flatMap(optional -> {
                                            if (optional.isPresent()) {
                                                CartItem existing = optional.get();
                                                existing.setQuantity(existing.getQuantity() + request.getQuantity());
                                                existing.setPrice(product.getPrice()); // Dynamic price
                                                existing.setUpdatedAt(LocalDateTime.now());
                                                return cartItemRepository.save(existing).then(Mono.just(true));
                                            } else {
                                                CartItem cartItem = new CartItem();
                                                cartItem.setUserId(userId);
                                                cartItem.setProductId(request.getProductId());
                                                cartItem.setQuantity(request.getQuantity());
                                                cartItem.setPrice(product.getPrice()); // Dynamic price
                                                cartItem.setCreatedAt(LocalDateTime.now());
                                                cartItem.setUpdatedAt(LocalDateTime.now());
                                                return cartItemRepository.save(cartItem).then(Mono.just(true));
                                            }
                                        });
                            })
                            .defaultIfEmpty(false); // Product not found
                })
                .defaultIfEmpty(false) // User not found
                .onErrorResume(e -> Mono.just(false));
    }

    @Override
    public Mono<Boolean> removeFromCart(String userId, String productId) {
        return cartItemRepository.findByUserIdAndProductId(userId, productId)
                .flatMap(optional -> {
                    if (optional.isPresent()) {
                        return cartItemRepository.delete(optional.get()).then(Mono.just(true));
                    }
                    return Mono.just(false);
                });
    }

    @Override
    public Flux<CartItemResponse> getCart(String userId) {
        return cartItemRepository.findByUserId(userId)
                .map(this::mapToCartItemResponse);
    }

    @Override
    @Transactional
    public Mono<OrderResponse> createOrder(String userId) {
        return cartItemRepository.findByUserId(userId)
                .collectList()
                .flatMap(cartItems -> {
                    if (cartItems.isEmpty()) return Mono.empty();
                    Order order = new Order();
                    order.setUserId(userId);
                    order.setItems(cartItems.stream()
                            .map(cartItem -> {
                                OrderItem item = new OrderItem();
                                item.setProductId(cartItem.getProductId());
                                item.setQuantity(cartItem.getQuantity());
                                item.setPrice(cartItem.getPrice());
                                return item;
                            })
                            .collect(Collectors.toList()));
                    order.calculateTotalAmount();
                    order.setCreatedAt(LocalDateTime.now());
                    order.setUpdatedAt(LocalDateTime.now());
                    return orderRepository.save(order)
                            .flatMap(savedOrder -> {
                                // Clear cart after order creation
                                return cartItemRepository.deleteByUserId(userId)
                                        .then(Mono.just(mapToOrderResponse(savedOrder)));
                            });
                });
    }

    private CartItemResponse mapToCartItemResponse(CartItem cartItem) {
        CartItemResponse response = new CartItemResponse();
        response.setId(cartItem.getId());
        response.setProductId(cartItem.getProductId());
        response.setQuantity(cartItem.getQuantity());
        response.setPrice(cartItem.getPrice());
        response.setTotalPrice(cartItem.getTotalPrice());
        return response;
    }

    private OrderResponse mapToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setUserId(order.getUserId());
        response.setTotalAmount(order.getTotalAmount());
        response.setStatus(order.getStatus());
        response.setItems(order.getItems().stream()
                .map(item -> {
                    OrderItemDTO dto = new OrderItemDTO();
                    dto.setId(item.getId());
                    dto.setProductId(item.getProductId());
                    dto.setQuantity(item.getQuantity());
                    dto.setPrice(item.getPrice());
                    dto.setTotalPrice(item.getTotalPrice());
                    return dto;
                })
                .collect(Collectors.toList()));
        return response;
    }
}
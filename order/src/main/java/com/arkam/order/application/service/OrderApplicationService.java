package com.arkam.order.application.service;

import com.arkam.order.application.dto.CartItemRequest;
import com.arkam.order.application.dto.OrderResponse;
import com.arkam.order.application.dto.ProductResponse;
import com.arkam.order.application.dto.UserResponse;
import com.arkam.order.application.port.in.*;
import com.arkam.order.application.port.out.OrderRepositoryPort;
import com.arkam.order.infrastructure.adapter.out.ProductServiceClient;
import com.arkam.order.infrastructure.adapter.out.UserServiceClient;
import com.arkam.order.domain.model.Order;
import com.arkam.order.domain.model.OrderItem;
import com.arkam.order.domain.model.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderApplicationService implements CreateOrderUseCase, GetOrderUseCase, GetAllOrdersUseCase, GetOrdersByUserUseCase {

    private final OrderRepositoryPort orderRepository;
    private final UserServiceClient userServiceClient;
    private final ProductServiceClient productServiceClient;

    @Override
    public Mono<OrderResponse> createOrder(String userId, List<CartItemRequest> items) {
        // Validate user exists
        return userServiceClient.getUserDetails(userId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found")))
                .flatMap(user -> {
                    // Validate and get product details for all items
                    return Flux.fromIterable(items)
                            .flatMap(item -> productServiceClient.getProductDetails(item.getProductId())
                                    .switchIfEmpty(Mono.error(new IllegalArgumentException("Product not found: " + item.getProductId())))
                                    .map(product -> createOrderItem(product, item)))
                            .collectList()
                            .map(orderItems -> {
                                Order order = new Order();
                                order.setUserId(userId);
                                order.setItems(orderItems);
                                order.setTotalAmount(calculateTotal(orderItems));
                                order.setStatus(OrderStatus.PENDING);
                                order.setCreatedAt(LocalDateTime.now());
                                order.setUpdatedAt(LocalDateTime.now());
                                return order;
                            });
                })
                .flatMap(orderRepository::save)
                .map(this::mapToResponse);
    }

    @Override
    public Mono<OrderResponse> getOrder(String id) {
        return orderRepository.findById(id)
                .map(this::mapToResponse);
    }

    @Override
    public Flux<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .map(this::mapToResponse);
    }

    @Override
    public Flux<OrderResponse> getOrdersByUser(String userId) {
        return orderRepository.findByUserId(userId)
                .map(this::mapToResponse);
    }

    private OrderItem createOrderItem(ProductResponse product, CartItemRequest item) {
        OrderItem orderItem = new OrderItem();
        orderItem.setProductId(product.getId().toString());
        orderItem.setQuantity(item.getQuantity());
        orderItem.setPrice(product.getPrice());
        return orderItem;
    }

    private BigDecimal calculateTotal(List<OrderItem> items) {
        return items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private OrderResponse mapToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setUserId(order.getUserId());
        response.setTotalAmount(order.getTotalAmount());
        response.setStatus(order.getStatus().name());
        response.setItems(order.getItems() != null ?
                order.getItems().stream().map(this::mapOrderItem).collect(Collectors.toList()) : null);
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        return response;
    }

    private com.arkam.order.application.dto.OrderItemDTO mapOrderItem(OrderItem item) {
        com.arkam.order.application.dto.OrderItemDTO dto = new com.arkam.order.application.dto.OrderItemDTO();
        dto.setProductId(item.getProductId());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());
        return dto;
    }
}
package com.arkam.order.application.service;


import com.arkam.order.domain.dto.OrderCreatedEvent;
import com.arkam.order.domain.dto.OrderItemDTO;
import com.arkam.order.domain.dto.OrderResponse;
import com.arkam.order.domain.model.*;
import com.arkam.order.infrastructure.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.cloud.stream.function.StreamBridge;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class OrderService {
    private final CartService cartService;
    private final OrderRepository orderRepository;
    private final StreamBridge streamBridge;

    public Mono<OrderResponse> createOrder(String userId) {
        return cartService.getCartReactive(userId)
                .flatMap(cartResponse -> {
                    List<CartItem> cartItems = cartResponse.getCartItems();
                    if (cartItems.isEmpty()) {
                        return Mono.empty();
                    }

                    // Create order
                    Order order = new Order();
                    order.setUserId(userId);
                    order.setStatus(OrderStatus.CONFIRMED);
                    order.setTotalAmount(cartResponse.getTotalPrice());

                    List<OrderItem> orderItems = cartItems.stream()
                            .map(item -> new OrderItem(
                                    null,
                                    item.getProductId(),
                                    item.getQuantity(),
                                    item.getPrice(),
                                    order
                            ))
                            .toList();

                    order.setItems(orderItems);
                    
                    return Mono.fromCallable(() -> orderRepository.save(order))
                            .doOnNext(savedOrder -> {
                                // Clear the cart
                                cartService.clearCart(userId);

                                // Publish order created event
                                OrderCreatedEvent event = new OrderCreatedEvent(
                                        savedOrder.getId(),
                                        savedOrder.getUserId(),
                                        savedOrder.getStatus(),
                                        mapToOrderItemDTOs(savedOrder.getItems()),
                                        savedOrder.getTotalAmount(),
                                        savedOrder.getCreatedAt()
                                );
                                streamBridge.send("createOrder-out-0", event);
                            })
                            .map(this::mapToOrderResponse);
                });
    }

    private List<OrderItemDTO> mapToOrderItemDTOs(List<OrderItem> items) {
        return items.stream()
                .map(item -> new OrderItemDTO(
                        item.getId(),
                        item.getProductId(),
                        item.getQuantity(),
                        item.getPrice(),
                        item.getPrice().multiply(new BigDecimal(item.getQuantity()))
                )).collect(Collectors.toList());
    }

    private OrderResponse mapToOrderResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getItems().stream()
                        .map(orderItem -> new OrderItemDTO(
                                orderItem.getId(),
                                orderItem.getProductId(),
                                orderItem.getQuantity(),
                                orderItem.getPrice(),
                                orderItem.getPrice().multiply(new BigDecimal(orderItem.getQuantity()))
                        ))
                        .toList(),
                order.getCreatedAt()
        );
    }
}
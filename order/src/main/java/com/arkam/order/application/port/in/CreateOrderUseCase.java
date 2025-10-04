package com.arkam.order.application.port.in;

import com.arkam.order.application.dto.CartItemRequest;
import com.arkam.order.application.dto.OrderResponse;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CreateOrderUseCase {
    Mono<OrderResponse> createOrder(String userId, List<CartItemRequest> items);
}
package com.arkam.order.application.port.in;

import com.arkam.order.application.dto.OrderResponse;
import reactor.core.publisher.Mono;

public interface CreateOrderUseCase {
    Mono<OrderResponse> createOrder(String userId);
}
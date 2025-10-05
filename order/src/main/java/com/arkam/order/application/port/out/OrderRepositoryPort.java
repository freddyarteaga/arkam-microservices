package com.arkam.order.application.port.out;

import com.arkam.order.domain.model.Order;
import reactor.core.publisher.Mono;

public interface OrderRepositoryPort {
    Mono<Order> save(Order order);
}
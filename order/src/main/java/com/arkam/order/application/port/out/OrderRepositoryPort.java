package com.arkam.order.application.port.out;

import com.arkam.order.domain.model.Order;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderRepositoryPort {
    Mono<Order> save(Order order);
    Mono<Order> findById(String id);
    Flux<Order> findAll();
    Flux<Order> findByUserId(String userId);
    Mono<Order> findByIdAndUserId(String id, String userId);
}
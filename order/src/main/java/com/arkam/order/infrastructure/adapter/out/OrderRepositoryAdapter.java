package com.arkam.order.infrastructure.adapter.out;

import com.arkam.order.application.port.out.OrderRepositoryPort;
import com.arkam.order.domain.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepositoryPort {

    private final OrderRepository orderRepository;
    private final OrderPersistenceMapper mapper;

    @Override
    public Mono<Order> save(Order order) {
        var entity = mapper.toEntity(order);
        return orderRepository.save(entity)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Order> findById(String id) {
        return orderRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Order> findAll() {
        return orderRepository.findAll()
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Order> findByUserId(String userId) {
        return orderRepository.findByUserId(userId)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Order> findByIdAndUserId(String id, String userId) {
        return orderRepository.findByIdAndUserId(id, userId)
                .map(mapper::toDomain);
    }
}
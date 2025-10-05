package com.arkam.order.infrastructure.adapter.persistence;

import com.arkam.order.application.port.out.OrderRepositoryPort;
import com.arkam.order.domain.model.Order;
import com.arkam.order.infrastructure.adapter.persistence.entity.OrderEntity;
import com.arkam.order.infrastructure.adapter.persistence.mapper.OrderPersistenceMapper;
import com.arkam.order.infrastructure.adapter.persistence.repository.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepositoryPort {

    private final OrderJpaRepository orderJpaRepository;
    private final OrderPersistenceMapper orderPersistenceMapper;

    @Override
    public Mono<Order> save(Order order) {
        return Mono.fromCallable(() -> {
            OrderEntity entity = orderPersistenceMapper.toEntity(order);
            OrderEntity savedEntity = orderJpaRepository.save(entity);
            return orderPersistenceMapper.toDomain(savedEntity);
        });
    }
}
package com.arkam.order.infrastructure.adapter.out;

import com.arkam.order.infrastructure.OrderEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface OrderRepository extends ReactiveMongoRepository<OrderEntity, String> {
    Flux<OrderEntity> findByUserId(String userId);
    Mono<OrderEntity> findByIdAndUserId(String id, String userId);
}
package com.arkam.product.infrastructure.adapter.out;

import com.arkam.product.infrastructure.ProductEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ProductRepository extends ReactiveMongoRepository<ProductEntity, String> {
    Flux<ProductEntity> findByActiveTrue();

    Flux<ProductEntity> findByActiveTrueAndStockQuantityGreaterThanAndNameContainingIgnoreCase(int stockQuantity, String keyword);

    Mono<ProductEntity> findByIdAndActiveTrue(String id);
}

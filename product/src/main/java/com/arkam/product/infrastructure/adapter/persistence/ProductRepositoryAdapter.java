package com.arkam.product.infrastructure.adapter.persistence;

import com.arkam.product.application.port.out.ProductRepositoryPort;
import com.arkam.product.domain.model.Product;
import com.arkam.product.infrastructure.adapter.persistence.entity.ProductEntity;
import com.arkam.product.infrastructure.adapter.persistence.mapper.ProductPersistenceMapper;
import com.arkam.product.infrastructure.adapter.persistence.repository.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepositoryPort {

    private final ProductJpaRepository productJpaRepository;
    private final ProductPersistenceMapper productPersistenceMapper;

    @Override
    public Mono<Product> save(Product product) {
        return Mono.fromCallable(() -> {
            ProductEntity entity = productPersistenceMapper.toEntity(product);
            ProductEntity savedEntity = productJpaRepository.save(entity);
            return productPersistenceMapper.toDomain(savedEntity);
        });
    }

    @Override
    public Mono<Optional<Product>> findById(Long id) {
        return Mono.fromCallable(() -> productJpaRepository.findById(id)
                .map(productPersistenceMapper::toDomain));
    }

    @Override
    public Flux<Product> findAll() {
        return Mono.fromCallable(() -> productJpaRepository.findByActiveTrue().stream()
                .map(productPersistenceMapper::toDomain)
                .collect(Collectors.toList()))
                .flatMapMany(Flux::fromIterable);
    }

    @Override
    public Flux<Product> searchProducts(String keyword) {
        return Mono.fromCallable(() -> productJpaRepository.searchProducts(keyword).stream()
                .map(productPersistenceMapper::toDomain)
                .collect(Collectors.toList()))
                .flatMapMany(Flux::fromIterable);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return Mono.fromRunnable(() -> productJpaRepository.deleteById(id));
    }
}
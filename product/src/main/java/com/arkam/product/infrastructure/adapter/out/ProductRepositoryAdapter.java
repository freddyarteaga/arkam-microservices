package com.arkam.product.infrastructure.adapter.out;

import com.arkam.product.application.port.out.ProductRepositoryPort;
import com.arkam.product.domain.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepositoryPort {

    private final ProductRepository productRepository;
    private final ProductPersistenceMapper mapper;

    @Override
    public Mono<Product> save(Product product) {
        var entity = mapper.toEntity(product);
        return productRepository.save(entity)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Product> findById(String id) {
        return productRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Product> findByActiveTrue() {
        return productRepository.findByActiveTrue()
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Product> searchProducts(String keyword) {
        return productRepository.findByActiveTrueAndStockQuantityGreaterThanAndNameContainingIgnoreCase(0, keyword)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Product> findByIdAndActiveTrue(String id) {
        return productRepository.findByIdAndActiveTrue(id)
                .map(mapper::toDomain);
    }
}
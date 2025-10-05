package com.arkam.product.application.port.out;

import com.arkam.product.domain.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface ProductRepositoryPort {
    Mono<Product> save(Product product);
    Mono<Optional<Product>> findById(Long id);
    Flux<Product> findAll();
    Flux<Product> searchProducts(String keyword);
    Mono<Void> deleteById(Long id);
}
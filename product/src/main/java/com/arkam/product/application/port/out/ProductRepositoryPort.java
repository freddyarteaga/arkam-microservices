package com.arkam.product.application.port.out;

import com.arkam.product.domain.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductRepositoryPort {
    Mono<Product> save(Product product);
    Mono<Product> findById(String id);
    Flux<Product> findByActiveTrue();
    Flux<Product> searchProducts(String keyword);
    Mono<Product> findByIdAndActiveTrue(String id);
}
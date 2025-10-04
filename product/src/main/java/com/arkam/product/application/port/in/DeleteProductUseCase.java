package com.arkam.product.application.port.in;

import reactor.core.publisher.Mono;

public interface DeleteProductUseCase {
    Mono<Boolean> deleteProduct(String id);
}
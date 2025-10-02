package com.arkam.product.application.port.in;

import reactor.core.publisher.Mono;

public interface DeleteProductUseCase {
    void deleteProduct(Long id);
    Mono<Void> deleteProductReactive(Long id);
}

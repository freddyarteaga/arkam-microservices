package com.arkam.product.application.port.in;

import com.arkam.product.application.dto.ProductRequest;
import reactor.core.publisher.Mono;

public interface UpdateProductUseCase {
    Mono<Boolean> updateProduct(Long id, ProductRequest request);
}
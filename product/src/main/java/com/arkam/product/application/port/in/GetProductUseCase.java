package com.arkam.product.application.port.in;

import com.arkam.product.application.dto.ProductResponse;
import reactor.core.publisher.Mono;

public interface GetProductUseCase {
    Mono<ProductResponse> getProduct(String id);
}
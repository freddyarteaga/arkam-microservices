package com.arkam.product.application.port.in;

import com.arkam.product.application.dto.ProductRequest;
import com.arkam.product.application.dto.ProductResponse;
import reactor.core.publisher.Mono;

public interface UpdateProductUseCase {
    Mono<ProductResponse> updateProduct(String id, ProductRequest request);
}
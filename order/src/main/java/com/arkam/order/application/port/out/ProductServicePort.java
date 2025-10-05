package com.arkam.order.application.port.out;

import com.arkam.order.application.dto.ProductResponse;
import reactor.core.publisher.Mono;

public interface ProductServicePort {
    Mono<ProductResponse> getProductDetails(String productId);
}
package com.arkam.product.application.port.in;

import com.arkam.product.application.dto.ProductResponse;
import reactor.core.publisher.Flux;

public interface SearchProductsUseCase {
    Flux<ProductResponse> searchProducts(String keyword);
}
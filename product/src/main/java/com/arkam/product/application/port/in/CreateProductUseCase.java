package com.arkam.product.application.port.in;

import com.arkam.product.application.dto.request.CreateProductRequestDto;
import com.arkam.product.application.dto.response.ProductResponseDto;
import reactor.core.publisher.Mono;

public interface CreateProductUseCase {
    ProductResponseDto createProduct(CreateProductRequestDto requestDto);
    Mono<ProductResponseDto> createProductReactive(CreateProductRequestDto requestDto);
}

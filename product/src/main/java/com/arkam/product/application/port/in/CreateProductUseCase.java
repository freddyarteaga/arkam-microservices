package com.arkam.product.application.port.in;

import com.arkam.product.application.dto.request.CreateProductRequestDto;
import com.arkam.product.application.dto.response.ProductResponseDto;

public interface CreateProductUseCase {
    ProductResponseDto createProduct(CreateProductRequestDto requestDto);
}

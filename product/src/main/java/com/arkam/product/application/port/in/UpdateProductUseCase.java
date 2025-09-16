package com.arkam.product.application.port.in;

import com.arkam.product.application.dto.request.UpdateProductRequestDto;
import com.arkam.product.application.dto.response.ProductResponseDto;

public interface UpdateProductUseCase {
    ProductResponseDto updateProduct(Long id, UpdateProductRequestDto requestDto);
}

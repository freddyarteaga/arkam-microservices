package com.arkam.product.application.port.in;

import com.arkam.product.application.dto.response.ProductResponseDto;

import java.util.List;

public interface GetProductUseCase {
    ProductResponseDto findProductById(Long id);
    List<ProductResponseDto> findAllActiveProducts();
    List<ProductResponseDto> searchProducts(String keyword);
}

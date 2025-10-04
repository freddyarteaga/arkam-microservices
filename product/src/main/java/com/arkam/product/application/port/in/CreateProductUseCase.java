package com.arkam.product.application.port.in;

import com.arkam.product.application.dto.ProductRequest;
import com.arkam.product.application.dto.ProductResponse;

public interface CreateProductUseCase {
    ProductResponse createProduct(ProductRequest request);
}
package com.arkam.product.application.port.in;

import com.arkam.product.application.dto.ProductRequest;
import com.arkam.product.application.dto.ProductResponse;
import java.util.Optional;

public interface UpdateProductUseCase {
    Optional<ProductResponse> updateProduct(Long id, ProductRequest request);
}
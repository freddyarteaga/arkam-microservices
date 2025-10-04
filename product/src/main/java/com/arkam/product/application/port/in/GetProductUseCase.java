package com.arkam.product.application.port.in;

import com.arkam.product.application.dto.ProductResponse;
import java.util.Optional;

public interface GetProductUseCase {
    Optional<ProductResponse> getProduct(String id);
}
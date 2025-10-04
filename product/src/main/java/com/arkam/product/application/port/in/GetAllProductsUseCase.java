package com.arkam.product.application.port.in;

import com.arkam.product.application.dto.ProductResponse;
import java.util.List;

public interface GetAllProductsUseCase {
    List<ProductResponse> getAllProducts();
}
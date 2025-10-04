package com.arkam.product.application.port.in;

import com.arkam.product.application.dto.ProductResponse;
import java.util.List;

public interface SearchProductsUseCase {
    List<ProductResponse> searchProducts(String keyword);
}
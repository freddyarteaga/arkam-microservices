package com.arkam.product.application.port.in;

import com.arkam.product.application.dto.response.ProductResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface GetProductUseCase {
    ProductResponseDto findProductById(Long id);
    List<ProductResponseDto> findAllActiveProducts();
    List<ProductResponseDto> searchProducts(String keyword);
    
    // Reactive methods
    Mono<ProductResponseDto> findProductByIdReactive(Long id);
    Flux<ProductResponseDto> findAllActiveProductsReactive();
    Flux<ProductResponseDto> searchProductsReactive(String keyword);
}

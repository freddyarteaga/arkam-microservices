package com.arkam.order.infrastructure.adapter.out;

import com.arkam.order.application.dto.ProductResponse;
import com.arkam.order.application.port.out.ProductServicePort;
import com.arkam.order.clients.ProductServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ProductServiceAdapter implements ProductServicePort {

    private final ProductServiceClient productServiceClient;

    @Override
    public Mono<ProductResponse> getProductDetails(String productId) {
        return productServiceClient.getProductDetails(productId);
    }
}
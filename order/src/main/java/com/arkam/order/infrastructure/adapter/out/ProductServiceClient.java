package com.arkam.order.infrastructure.adapter.out;

import com.arkam.order.application.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ProductServiceClient {

    private final WebClient productServiceWebClient;

    public Mono<ProductResponse> getProductDetails(String id) {
        return productServiceWebClient.get()
                .uri("/api/products/{id}", id)
                .retrieve()
                .bodyToMono(ProductResponse.class)
                .onErrorResume(ex -> Mono.empty()); // Handle errors gracefully
    }
}

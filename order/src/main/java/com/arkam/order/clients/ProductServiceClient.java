package com.arkam.order.clients;

import com.arkam.order.application.dto.ProductResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import reactor.core.publisher.Mono;

@HttpExchange
public interface ProductServiceClient {

    @GetExchange("/api/products/{id}")
    Mono<ProductResponse> getProductDetails(@PathVariable String id);
}

package com.arkam.order.application.port.in;

import com.arkam.order.application.dto.CartItemRequest;
import reactor.core.publisher.Mono;

public interface AddToCartUseCase {
    Mono<Boolean> addToCart(String userId, CartItemRequest request);
}
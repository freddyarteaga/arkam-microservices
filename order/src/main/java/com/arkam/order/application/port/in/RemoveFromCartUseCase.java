package com.arkam.order.application.port.in;

import reactor.core.publisher.Mono;

public interface RemoveFromCartUseCase {
    Mono<Boolean> removeFromCart(String userId, String productId);
}
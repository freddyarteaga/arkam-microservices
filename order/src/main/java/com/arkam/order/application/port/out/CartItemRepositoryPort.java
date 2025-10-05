package com.arkam.order.application.port.out;

import com.arkam.order.domain.model.CartItem;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface CartItemRepositoryPort {
    Mono<CartItem> save(CartItem cartItem);
    Mono<Optional<CartItem>> findByUserIdAndProductId(String userId, String productId);
    Flux<CartItem> findByUserId(String userId);
    Mono<Void> delete(CartItem cartItem);
    Mono<Void> deleteByUserId(String userId);
}
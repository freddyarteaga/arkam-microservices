package com.arkam.order.application.port.in;

import com.arkam.order.application.dto.CartItemResponse;
import reactor.core.publisher.Flux;

public interface GetCartUseCase {
    Flux<CartItemResponse> getCart(String userId);
}
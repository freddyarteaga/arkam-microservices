package com.arkam.order.application.port.in;

import com.arkam.order.application.dto.OrderResponse;
import reactor.core.publisher.Flux;

public interface GetAllOrdersUseCase {
    Flux<OrderResponse> getAllOrders();
}
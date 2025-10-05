package com.arkam.order.infrastructure.adapter.in;

import com.arkam.order.application.dto.OrderResponse;
import com.arkam.order.application.port.in.CreateOrderUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;

    @PostMapping
    public Mono<ResponseEntity<OrderResponse>> createOrder(@RequestHeader("X-User-ID") String userId) {
        return createOrderUseCase.createOrder(userId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }
}
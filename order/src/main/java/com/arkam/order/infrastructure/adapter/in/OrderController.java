package com.arkam.order.infrastructure.adapter.in;

import com.arkam.order.application.dto.CartItemRequest;
import com.arkam.order.application.dto.OrderResponse;
import com.arkam.order.application.port.in.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final GetAllOrdersUseCase getAllOrdersUseCase;
    private final GetOrdersByUserUseCase getOrdersByUserUseCase;

    @PostMapping
    public Mono<ResponseEntity<OrderResponse>> createOrder(
            @RequestParam String userId,
            @RequestBody List<CartItemRequest> items) {
        return createOrderUseCase.createOrder(userId, items)
                .map(order -> ResponseEntity.status(HttpStatus.CREATED).body(order))
                .onErrorResume(IllegalArgumentException.class,
                        ex -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<OrderResponse>> getOrder(@PathVariable String id) {
        return getOrderUseCase.getOrder(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Flux<OrderResponse> getAllOrders() {
        return getAllOrdersUseCase.getAllOrders();
    }

    @GetMapping("/user/{userId}")
    public Flux<OrderResponse> getOrdersByUser(@PathVariable String userId) {
        return getOrdersByUserUseCase.getOrdersByUser(userId);
    }
}
package com.arkam.order.infrastructure.adapter.in;

import com.arkam.order.application.dto.CartItemRequest;
import com.arkam.order.application.dto.CartItemResponse;
import com.arkam.order.application.port.in.AddToCartUseCase;
import com.arkam.order.application.port.in.GetCartUseCase;
import com.arkam.order.application.port.in.RemoveFromCartUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {

    private final AddToCartUseCase addToCartUseCase;
    private final RemoveFromCartUseCase removeFromCartUseCase;
    private final GetCartUseCase getCartUseCase;

    @PostMapping
    public Mono<ResponseEntity<String>> addToCart(
            @RequestHeader("X-User-ID") String userId,
            @RequestBody CartItemRequest request) {
        return addToCartUseCase.addToCart(userId, request)
                .map(success -> success ?
                        ResponseEntity.ok("Producto agregado al carrito exitosamente") :
                        ResponseEntity.badRequest().body("No se pudo agregar al carrito: usuario, producto no encontrado o stock insuficiente"));
    }

    @DeleteMapping("/items/{productId}")
    public Mono<ResponseEntity<Void>> removeFromCart(
            @RequestHeader("X-User-ID") String userId,
            @PathVariable String productId) {
        return removeFromCartUseCase.removeFromCart(userId, productId)
                .map(success -> success ?
                        ResponseEntity.noContent().build() :
                        ResponseEntity.notFound().build());
    }

    @GetMapping
    public Flux<CartItemResponse> getCart(@RequestHeader("X-User-ID") String userId) {
        return getCartUseCase.getCart(userId);
    }
}
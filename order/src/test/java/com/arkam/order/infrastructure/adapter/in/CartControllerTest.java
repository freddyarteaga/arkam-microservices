package com.arkam.order.infrastructure.adapter.in;

import com.arkam.order.application.dto.CartItemRequest;
import com.arkam.order.application.dto.CartItemResponse;
import com.arkam.order.application.port.in.AddToCartUseCase;
import com.arkam.order.application.port.in.GetCartUseCase;
import com.arkam.order.application.port.in.RemoveFromCartUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(CartController.class)
class CartControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private AddToCartUseCase addToCartUseCase;

    @MockBean
    private RemoveFromCartUseCase removeFromCartUseCase;

    @MockBean
    private GetCartUseCase getCartUseCase;

    private CartItemRequest cartItemRequest;
    private CartItemResponse cartItemResponse;

    @BeforeEach
    void setUp() {
        cartItemRequest = new CartItemRequest();
        cartItemRequest.setProductId("prod1");
        cartItemRequest.setQuantity(1);

        cartItemResponse = new CartItemResponse();
        cartItemResponse.setId(1L);
        cartItemResponse.setProductId("prod1");
    }

    @Test
    void testAddToCart() {
        when(addToCartUseCase.addToCart(anyString(), any(CartItemRequest.class))).thenReturn(Mono.just(true));

        webTestClient.post()
                .uri("/api/cart")
                .header("X-User-ID", "user1")
                .bodyValue(cartItemRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Producto agregado al carrito exitosamente");
    }

    @Test
    void testGetCart() {
        when(getCartUseCase.getCart("user1")).thenReturn(Flux.just(cartItemResponse));

        webTestClient.get()
                .uri("/api/cart")
                .header("X-User-ID", "user1")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CartItemResponse.class)
                .hasSize(1);
    }

    @Test
    void testRemoveFromCart() {
        when(removeFromCartUseCase.removeFromCart("user1", "prod1")).thenReturn(Mono.just(true));

        webTestClient.delete()
                .uri("/api/cart/items/prod1")
                .header("X-User-ID", "user1")
                .exchange()
                .expectStatus().isNoContent();
    }
}
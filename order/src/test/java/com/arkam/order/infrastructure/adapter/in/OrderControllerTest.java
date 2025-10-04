package com.arkam.order.infrastructure.adapter.in;

import com.arkam.order.application.dto.CartItemRequest;
import com.arkam.order.application.dto.OrderResponse;
import com.arkam.order.application.port.in.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CreateOrderUseCase createOrderUseCase;

    @MockBean
    private GetOrderUseCase getOrderUseCase;

    @MockBean
    private GetAllOrdersUseCase getAllOrdersUseCase;

    @MockBean
    private GetOrdersByUserUseCase getOrdersByUserUseCase;

    private CartItemRequest cartItemRequest;
    private OrderResponse orderResponse;

    @BeforeEach
    void setUp() {
        cartItemRequest = new CartItemRequest();
        cartItemRequest.setProductId("prod123");
        cartItemRequest.setQuantity(2);

        orderResponse = new OrderResponse();
        orderResponse.setId("order123");
        orderResponse.setUserId("user123");
        orderResponse.setTotalAmount(BigDecimal.valueOf(199.98));
        orderResponse.setStatus("PENDING");
        orderResponse.setCreatedAt(LocalDateTime.now());
        orderResponse.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void createOrder_Success() {
        // Given
        List<CartItemRequest> items = List.of(cartItemRequest);
        when(createOrderUseCase.createOrder(anyString(), any(List.class)))
                .thenReturn(Mono.just(orderResponse));

        // When & Then
        webTestClient.post()
                .uri("/api/orders?userId=user123")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(items)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(OrderResponse.class)
                .isEqualTo(orderResponse);
    }

    @Test
    void createOrder_UserNotFound() {
        // Given
        List<CartItemRequest> items = List.of(cartItemRequest);
        when(createOrderUseCase.createOrder(anyString(), any(List.class)))
                .thenReturn(Mono.error(new IllegalArgumentException("User not found")));

        // When & Then
        webTestClient.post()
                .uri("/api/orders?userId=user123")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(items)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getOrder_Success() {
        // Given
        when(getOrderUseCase.getOrder("order123"))
                .thenReturn(Mono.just(orderResponse));

        // When & Then
        webTestClient.get()
                .uri("/api/orders/order123")
                .exchange()
                .expectStatus().isOk()
                .expectBody(OrderResponse.class)
                .isEqualTo(orderResponse);
    }

    @Test
    void getOrder_NotFound() {
        // Given
        when(getOrderUseCase.getOrder("nonexistent"))
                .thenReturn(Mono.empty());

        // When & Then
        webTestClient.get()
                .uri("/api/orders/nonexistent")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getAllOrders_Success() {
        // Given
        OrderResponse orderResponse2 = new OrderResponse();
        orderResponse2.setId("order456");
        orderResponse2.setUserId("user456");
        orderResponse2.setTotalAmount(BigDecimal.valueOf(299.97));
        orderResponse2.setStatus("PENDING");

        when(getAllOrdersUseCase.getAllOrders())
                .thenReturn(Flux.just(orderResponse, orderResponse2));

        // When & Then
        webTestClient.get()
                .uri("/api/orders")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(OrderResponse.class)
                .hasSize(2);
    }

    @Test
    void getOrdersByUser_Success() {
        // Given
        when(getOrdersByUserUseCase.getOrdersByUser("user123"))
                .thenReturn(Flux.just(orderResponse));

        // When & Then
        webTestClient.get()
                .uri("/api/orders/user/user123")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(OrderResponse.class)
                .hasSize(1)
                .contains(orderResponse);
    }
}
package com.arkam.order.application.service;

import com.arkam.order.application.dto.CartItemRequest;
import com.arkam.order.application.dto.OrderResponse;
import com.arkam.order.application.dto.ProductResponse;
import com.arkam.order.application.dto.UserResponse;
import com.arkam.order.application.port.out.OrderRepositoryPort;
import com.arkam.order.domain.model.Order;
import com.arkam.order.domain.model.OrderItem;
import com.arkam.order.domain.model.OrderStatus;
import com.arkam.order.infrastructure.adapter.out.ProductServiceClient;
import com.arkam.order.infrastructure.adapter.out.UserServiceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderApplicationServiceTest {

    @Mock
    private OrderRepositoryPort orderRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private ProductServiceClient productServiceClient;

    @InjectMocks
    private OrderApplicationService orderApplicationService;

    private UserResponse userResponse;
    private ProductResponse productResponse;
    private CartItemRequest cartItemRequest;
    private Order order;
    private OrderResponse orderResponse;

    @BeforeEach
    void setUp() {
        userResponse = new UserResponse();
        userResponse.setId("user123");
        userResponse.setFirstName("John");
        userResponse.setLastName("Doe");
        userResponse.setEmail("john.doe@example.com");

        productResponse = new ProductResponse();
        productResponse.setId(123L);
        productResponse.setName("Test Product");
        productResponse.setPrice(BigDecimal.valueOf(99.99));
        productResponse.setStockQuantity(10);
        productResponse.setActive(true);

        cartItemRequest = new CartItemRequest();
        cartItemRequest.setProductId("prod123");
        cartItemRequest.setQuantity(2);

        OrderItem orderItem = new OrderItem();
        orderItem.setProductId("prod123");
        orderItem.setQuantity(2);
        orderItem.setPrice(BigDecimal.valueOf(99.99));

        order = new Order();
        order.setId("order123");
        order.setUserId("user123");
        order.setItems(List.of(orderItem));
        order.setTotalAmount(BigDecimal.valueOf(199.98));
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        orderResponse = new OrderResponse();
        orderResponse.setId("order123");
        orderResponse.setUserId("user123");
        orderResponse.setTotalAmount(BigDecimal.valueOf(199.98));
        orderResponse.setStatus("PENDING");
        orderResponse.setItems(List.of(new com.arkam.order.application.dto.OrderItemDTO()));
        orderResponse.setCreatedAt(order.getCreatedAt());
        orderResponse.setUpdatedAt(order.getUpdatedAt());
    }

    @Test
    void createOrder_Success() {
        // Given
        List<CartItemRequest> items = List.of(cartItemRequest);

        when(userServiceClient.getUserDetails("user123")).thenReturn(Mono.just(userResponse));
        when(productServiceClient.getProductDetails("prod123")).thenReturn(Mono.just(productResponse));
        when(orderRepository.save(any(Order.class))).thenReturn(Mono.just(order));

        // When
        Mono<OrderResponse> result = orderApplicationService.createOrder("user123", items);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response ->
                        response.getUserId().equals("user123") &&
                        response.getTotalAmount().compareTo(BigDecimal.valueOf(199.98)) == 0 &&
                        response.getStatus().equals("PENDING"))
                .verifyComplete();
    }

    @Test
    void createOrder_UserNotFound() {
        // Given
        List<CartItemRequest> items = List.of(cartItemRequest);

        when(userServiceClient.getUserDetails("nonexistent")).thenReturn(Mono.empty());

        // When
        Mono<OrderResponse> result = orderApplicationService.createOrder("nonexistent", items);

        // Then
        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void createOrder_ProductNotFound() {
        // Given
        List<CartItemRequest> items = List.of(cartItemRequest);

        when(userServiceClient.getUserDetails("user123")).thenReturn(Mono.just(userResponse));
        when(productServiceClient.getProductDetails("prod123")).thenReturn(Mono.empty());

        // When
        Mono<OrderResponse> result = orderApplicationService.createOrder("user123", items);

        // Then
        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void getOrder_Success() {
        // Given
        when(orderRepository.findById("order123")).thenReturn(Mono.just(order));

        // When
        Mono<OrderResponse> result = orderApplicationService.getOrder("order123");

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response ->
                        response.getId().equals("order123") &&
                        response.getUserId().equals("user123"))
                .verifyComplete();
    }

    @Test
    void getOrder_NotFound() {
        // Given
        when(orderRepository.findById("nonexistent")).thenReturn(Mono.empty());

        // When
        Mono<OrderResponse> result = orderApplicationService.getOrder("nonexistent");

        // Then
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void getAllOrders_Success() {
        // Given
        Order order2 = new Order();
        order2.setId("order456");
        order2.setUserId("user456");
        order2.setTotalAmount(BigDecimal.valueOf(299.97));
        order2.setStatus(OrderStatus.PENDING);

        when(orderRepository.findAll()).thenReturn(Flux.just(order, order2));

        // When
        Flux<OrderResponse> result = orderApplicationService.getAllOrders();

        // Then
        StepVerifier.create(result)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void getOrdersByUser_Success() {
        // Given
        when(orderRepository.findByUserId("user123")).thenReturn(Flux.just(order));

        // When
        Flux<OrderResponse> result = orderApplicationService.getOrdersByUser("user123");

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> response.getUserId().equals("user123"))
                .verifyComplete();
    }
}
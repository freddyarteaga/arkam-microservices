package com.arkam.order.application.service;

import com.arkam.order.application.dto.CartItemRequest;
import com.arkam.order.application.dto.CartItemResponse;
import com.arkam.order.application.dto.ProductResponse;
import com.arkam.order.application.dto.UserResponse;
import com.arkam.order.application.port.out.CartItemRepositoryPort;
import com.arkam.order.application.port.out.OrderRepositoryPort;
import com.arkam.order.application.port.out.ProductServicePort;
import com.arkam.order.application.port.out.UserServicePort;
import com.arkam.order.domain.model.CartItem;
import com.arkam.order.domain.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class OrderApplicationServiceTest {

    @Mock
    private CartItemRepositoryPort cartItemRepository;

    @Mock
    private OrderRepositoryPort orderRepository;

    @Mock
    private ProductServicePort productService;

    @Mock
    private UserServicePort userService;

    @InjectMocks
    private OrderApplicationService orderApplicationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddToCart() {
        CartItemRequest request = new CartItemRequest();
        request.setProductId("prod1");
        request.setQuantity(1);

        UserResponse user = new UserResponse();
        user.setId("user1");

        ProductResponse product = new ProductResponse();
        product.setPrice(BigDecimal.valueOf(100.0));

        CartItem cartItem = new CartItem();
        cartItem.setId(1L);

        when(userService.getUserDetails("user1")).thenReturn(Mono.just(user));
        when(productService.getProductDetails("prod1")).thenReturn(Mono.just(product));
        when(cartItemRepository.findByUserIdAndProductId("user1", "prod1")).thenReturn(Mono.just(Optional.empty()));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(Mono.just(cartItem));

        Mono<Boolean> result = orderApplicationService.addToCart("user1", request);

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void testGetCart() {
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);

        when(cartItemRepository.findByUserId("user1")).thenReturn(Flux.just(cartItem));

        Flux<CartItemResponse> result = orderApplicationService.getCart("user1");

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void testCreateOrder() {
        CartItem cartItem = new CartItem();
        cartItem.setProductId("prod1");
        cartItem.setQuantity(1);
        cartItem.setPrice(BigDecimal.valueOf(100.0));

        Order order = new Order();
        order.setId(1L);

        when(cartItemRepository.findByUserId("user1")).thenReturn(Flux.just(cartItem));
        when(orderRepository.save(any(Order.class))).thenReturn(Mono.just(order));
        when(cartItemRepository.deleteByUserId("user1")).thenReturn(Mono.empty());

        Mono<com.arkam.order.application.dto.OrderResponse> result = orderApplicationService.createOrder("user1");

        StepVerifier.create(result)
                .expectNextMatches(response -> response.getId().equals(1L))
                .verifyComplete();
    }
}
package com.arkam.order.application.service;

import com.arkam.order.domain.dto.CartResponse;
import com.arkam.order.domain.dto.OrderCreatedEvent;
import com.arkam.order.domain.dto.OrderItemDTO;
import com.arkam.order.domain.dto.OrderResponse;
import com.arkam.order.domain.model.*;
import com.arkam.order.infrastructure.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.stream.function.StreamBridge;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private CartService cartService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private StreamBridge streamBridge;

    @InjectMocks
    private OrderService orderService;

    private Order order;
    private List<CartItem> cartItems;
    private CartResponse cartResponse;
    private CartResponse emptyCartResponse;

    @BeforeEach
    void setUp() {
        // Setup cart items
        CartItem cartItem1 = new CartItem();
        cartItem1.setId(1L);
        cartItem1.setProductId("PROD-001");
        cartItem1.setQuantity(2);
        cartItem1.setPrice(new BigDecimal("10.00"));
        cartItem1.setUserId("USER-001");

        CartItem cartItem2 = new CartItem();
        cartItem2.setId(2L);
        cartItem2.setProductId("PROD-002");
        cartItem2.setQuantity(1);
        cartItem2.setPrice(new BigDecimal("15.00"));
        cartItem2.setUserId("USER-001");

        cartItems = Arrays.asList(cartItem1, cartItem2);

        // Setup order
        order = new Order();
        order.setId(1L);
        order.setUserId("USER-001");
        order.setStatus(OrderStatus.CONFIRMED);
        order.setTotalAmount(new BigDecimal("35.00"));
        order.setCreatedAt(LocalDateTime.now());

        OrderItem orderItem1 = new OrderItem();
        orderItem1.setId(1L);
        orderItem1.setProductId("PROD-001");
        orderItem1.setQuantity(2);
        orderItem1.setPrice(new BigDecimal("10.00"));
        orderItem1.setOrder(order);

        OrderItem orderItem2 = new OrderItem();
        orderItem2.setId(2L);
        orderItem2.setProductId("PROD-002");
        orderItem2.setQuantity(1);
        orderItem2.setPrice(new BigDecimal("15.00"));
        orderItem2.setOrder(order);

        order.setItems(Arrays.asList(orderItem1, orderItem2));

        cartResponse = CartResponse.builder()
                .cartItems(cartItems)
                .totalQuantity(3)
                .totalPrice(new BigDecimal("35.00"))
                .build();

        emptyCartResponse = CartResponse.builder()
                .cartItems(Collections.emptyList())
                .totalQuantity(0)
                .totalPrice(BigDecimal.ZERO)
                .build();
    }

    @Test
    void createOrder_WithValidCartItems_ShouldReturnOrderResponse() {
        // Given
        String userId = "USER-001";
        when(cartService.getCartReactive(userId)).thenReturn(Mono.just(cartResponse));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // When
        Optional<OrderResponse> result = orderService.createOrder(userId).blockOptional();

        // Then
        assertTrue(result.isPresent());
        OrderResponse orderResponse = result.get();
        assertEquals(order.getId(), orderResponse.getId());
        assertEquals(order.getTotalAmount(), orderResponse.getTotalAmount());
        assertEquals(order.getStatus(), orderResponse.getStatus());
        assertEquals(2, orderResponse.getItems().size());

        verify(orderRepository).save(any(Order.class));
        verify(cartService).clearCart(userId);
        verify(streamBridge).send(eq("createOrder-out-0"), any(OrderCreatedEvent.class));
    }

    @Test
    void createOrder_WithEmptyCart_ShouldReturnEmpty() {
        // Given
        String userId = "USER-001";
        when(cartService.getCartReactive(userId)).thenReturn(Mono.just(emptyCartResponse));

        // When
        Optional<OrderResponse> result = orderService.createOrder(userId).blockOptional();

        // Then
        assertFalse(result.isPresent());
        verify(orderRepository, never()).save(any(Order.class));
        verify(cartService, never()).clearCart(anyString());
        verify(streamBridge, never()).send(anyString(), any());
    }

    @Test
    void createOrder_ShouldCalculateTotalPriceCorrectly() {
        // Given
        String userId = "USER-001";
        when(cartService.getCartReactive(userId)).thenReturn(Mono.just(cartResponse));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order orderToSave = invocation.getArgument(0);
            orderToSave.setId(1L);
            return orderToSave;
        });

        // When
        Optional<OrderResponse> result = orderService.createOrder(userId).blockOptional();

        // Then
        assertTrue(result.isPresent());
        OrderResponse orderResponse = result.get();
        assertEquals(new BigDecimal("35.00"), orderResponse.getTotalAmount());
    }

    @Test
    void createOrder_ShouldMapOrderItemsCorrectly() {
        // Given
        String userId = "USER-001";
        when(cartService.getCartReactive(userId)).thenReturn(Mono.just(cartResponse));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order orderToSave = invocation.getArgument(0);
            orderToSave.setId(1L);
            return orderToSave;
        });

        // When
        Optional<OrderResponse> result = orderService.createOrder(userId).blockOptional();

        // Then
        assertTrue(result.isPresent());
        OrderResponse orderResponse = result.get();
        assertEquals(2, orderResponse.getItems().size());

        OrderItemDTO item1 = orderResponse.getItems().getFirst();
        assertEquals("PROD-001", item1.getProductId());
        assertEquals(2, item1.getQuantity());
        assertEquals(new BigDecimal("10.00"), item1.getPrice());
        assertEquals(new BigDecimal("20.00"), item1.getSubTotal());

        OrderItemDTO item2 = orderResponse.getItems().get(1);
        assertEquals("PROD-002", item2.getProductId());
        assertEquals(1, item2.getQuantity());
        assertEquals(new BigDecimal("15.00"), item2.getPrice());
        assertEquals(new BigDecimal("15.00"), item2.getSubTotal());
    }

    @Test
    void createOrder_ShouldPublishOrderCreatedEvent() {
        // Given
        String userId = "USER-001";
        when(cartService.getCartReactive(userId)).thenReturn(Mono.just(cartResponse));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order orderToSave = invocation.getArgument(0);
            orderToSave.setId(1L);
            orderToSave.setCreatedAt(LocalDateTime.now());
            return orderToSave;
        });

        // When
        orderService.createOrder(userId).block();

        // Then
        verify(streamBridge).send(eq("createOrder-out-0"), any(OrderCreatedEvent.class));
    }

    @Test
    void createOrder_ShouldSetOrderStatusAsConfirmed() {
        // Given
        String userId = "USER-001";
        when(cartService.getCartReactive(userId)).thenReturn(Mono.just(cartResponse));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order orderToSave = invocation.getArgument(0);
            orderToSave.setId(1L);
            return orderToSave;
        });

        // When
        Optional<OrderResponse> result = orderService.createOrder(userId).blockOptional();

        // Then
        assertTrue(result.isPresent());
        assertEquals(OrderStatus.CONFIRMED, result.get().getStatus());
    }
}

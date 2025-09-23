package com.arkam.order.web.controller;

import com.arkam.order.application.service.OrderService;
import com.arkam.order.domain.dto.OrderItemDTO;
import com.arkam.order.domain.dto.OrderResponse;
import com.arkam.order.domain.model.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private OrderResponse orderResponse;

    @BeforeEach
    void setUp() {
        OrderItemDTO item1 = new OrderItemDTO(
                1L,
                "PROD-001",
                2,
                new BigDecimal("10.00"),
                new BigDecimal("20.00")
        );

        OrderItemDTO item2 = new OrderItemDTO(
                2L,
                "PROD-002",
                1,
                new BigDecimal("15.00"),
                new BigDecimal("15.00")
        );

        orderResponse = new OrderResponse(
                1L,
                new BigDecimal("35.00"),
                OrderStatus.CONFIRMED,
                Arrays.asList(item1, item2),
                LocalDateTime.now()
        );
    }

    @Test
    void createOrder_WithValidUserId_ShouldReturnCreatedResponse() {
        // Given
        String userId = "USER-001";
        when(orderService.createOrder(userId)).thenReturn(Mono.empty());

        // When
        ResponseEntity<OrderResponse> response = orderController.createOrder(userId).block();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(orderResponse.getId(), response.getBody().getId());
        assertEquals(orderResponse.getTotalAmount(), response.getBody().getTotalAmount());
        assertEquals(orderResponse.getStatus(), response.getBody().getStatus());
        assertEquals(2, response.getBody().getItems().size());

        verify(orderService).createOrder(userId);
    }

    @Test
    void createOrder_WithEmptyOrderResponse_ShouldReturnBadRequest() {
        // Given
        String userId = "USER-001";
        when(orderService.createOrder(userId)).thenReturn(Mono.empty());

        // When
        ResponseEntity<OrderResponse> response = orderController.createOrder(userId).block();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());

        verify(orderService).createOrder(userId);
    }

    @Test
    void createOrder_WithNullUserId_ShouldCallServiceWithNull() {
        // Given
        String userId = null;
        when(orderService.createOrder(userId)).thenReturn(Mono.empty());

        // When
        ResponseEntity<OrderResponse> response = orderController.createOrder(userId).block();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(orderService).createOrder(userId);
    }

    @Test
    void createOrder_WithEmptyUserId_ShouldCallServiceWithEmptyString() {
        // Given
        String userId = "";
        when(orderService.createOrder(userId)).thenReturn(Mono.empty());

        // When
        ResponseEntity<OrderResponse> response = orderController.createOrder(userId).block();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(orderService).createOrder(userId);
    }
}

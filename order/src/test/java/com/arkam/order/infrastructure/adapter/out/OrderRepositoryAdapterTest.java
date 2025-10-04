package com.arkam.order.infrastructure.adapter.out;

import com.arkam.order.application.port.out.OrderRepositoryPort;
import com.arkam.order.domain.model.Order;
import com.arkam.order.domain.model.OrderItem;
import com.arkam.order.domain.model.OrderStatus;
import com.arkam.order.infrastructure.OrderEntity;
import com.arkam.order.infrastructure.OrderItemEntity;
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
class OrderRepositoryAdapterTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderPersistenceMapper mapper;

    @InjectMocks
    private OrderRepositoryAdapter orderRepositoryAdapter;

    private Order order;
    private OrderEntity orderEntity;

    @BeforeEach
    void setUp() {
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

        OrderItemEntity orderItemEntity = new OrderItemEntity();
        orderItemEntity.setId("item123");
        orderItemEntity.setProductId("prod123");
        orderItemEntity.setQuantity(2);
        orderItemEntity.setPrice(BigDecimal.valueOf(99.99));

        orderEntity = new OrderEntity();
        orderEntity.setId("order123");
        orderEntity.setUserId("user123");
        orderEntity.setItems(List.of(orderItemEntity));
        orderEntity.setTotalAmount(BigDecimal.valueOf(199.98));
        orderEntity.setStatus("PENDING");
        orderEntity.setCreatedAt(LocalDateTime.now());
        orderEntity.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void save_Success() {
        // Given
        when(mapper.toEntity(any(Order.class))).thenReturn(orderEntity);
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(Mono.just(orderEntity));
        when(mapper.toDomain(any(OrderEntity.class))).thenReturn(order);

        // When
        Mono<Order> result = orderRepositoryAdapter.save(order);

        // Then
        StepVerifier.create(result)
                .expectNext(order)
                .verifyComplete();
    }

    @Test
    void findById_Success() {
        // Given
        when(orderRepository.findById("order123")).thenReturn(Mono.just(orderEntity));
        when(mapper.toDomain(orderEntity)).thenReturn(order);

        // When
        Mono<Order> result = orderRepositoryAdapter.findById("order123");

        // Then
        StepVerifier.create(result)
                .expectNext(order)
                .verifyComplete();
    }

    @Test
    void findById_NotFound() {
        // Given
        when(orderRepository.findById("nonexistent")).thenReturn(Mono.empty());

        // When
        Mono<Order> result = orderRepositoryAdapter.findById("nonexistent");

        // Then
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void findAll_Success() {
        // Given
        Order order2 = new Order();
        order2.setId("order456");
        order2.setUserId("user456");
        order2.setTotalAmount(BigDecimal.valueOf(299.97));
        order2.setStatus(OrderStatus.PENDING);

        OrderEntity orderEntity2 = new OrderEntity();
        orderEntity2.setId("order456");
        orderEntity2.setUserId("user456");
        orderEntity2.setTotalAmount(BigDecimal.valueOf(299.97));
        orderEntity2.setStatus("PENDING");

        when(orderRepository.findAll()).thenReturn(Flux.just(orderEntity, orderEntity2));
        when(mapper.toDomain(orderEntity)).thenReturn(order);
        when(mapper.toDomain(orderEntity2)).thenReturn(order2);

        // When
        Flux<Order> result = orderRepositoryAdapter.findAll();

        // Then
        StepVerifier.create(result)
                .expectNext(order)
                .expectNext(order2)
                .verifyComplete();
    }

    @Test
    void findByUserId_Success() {
        // Given
        when(orderRepository.findByUserId("user123")).thenReturn(Flux.just(orderEntity));
        when(mapper.toDomain(orderEntity)).thenReturn(order);

        // When
        Flux<Order> result = orderRepositoryAdapter.findByUserId("user123");

        // Then
        StepVerifier.create(result)
                .expectNext(order)
                .verifyComplete();
    }

    @Test
    void findByIdAndUserId_Success() {
        // Given
        when(orderRepository.findByIdAndUserId("order123", "user123")).thenReturn(Mono.just(orderEntity));
        when(mapper.toDomain(orderEntity)).thenReturn(order);

        // When
        Mono<Order> result = ((OrderRepositoryPort) orderRepositoryAdapter).findByIdAndUserId("order123", "user123");

        // Then
        StepVerifier.create(result)
                .expectNext(order)
                .verifyComplete();
    }
}
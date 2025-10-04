package com.arkam.order.infrastructure.adapter.out;

import com.arkam.order.domain.model.Order;
import com.arkam.order.domain.model.OrderItem;
import com.arkam.order.domain.model.OrderStatus;
import com.arkam.order.infrastructure.OrderEntity;
import com.arkam.order.infrastructure.OrderItemEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderPersistenceMapper {

    public OrderEntity toEntity(Order order) {
        OrderEntity entity = new OrderEntity();
        entity.setId(order.getId());
        entity.setUserId(order.getUserId());
        entity.setTotalAmount(order.getTotalAmount());
        entity.setStatus(order.getStatus().name());
        entity.setItems(order.getItems() != null ?
                order.getItems().stream().map(this::toOrderItemEntity).collect(Collectors.toList()) : null);
        entity.setCreatedAt(order.getCreatedAt());
        entity.setUpdatedAt(order.getUpdatedAt());
        return entity;
    }

    public Order toDomain(OrderEntity entity) {
        Order order = new Order();
        order.setId(entity.getId());
        order.setUserId(entity.getUserId());
        order.setTotalAmount(entity.getTotalAmount());
        order.setStatus(OrderStatus.valueOf(entity.getStatus()));
        order.setItems(entity.getItems() != null ?
                entity.getItems().stream().map(this::toOrderItemDomain).collect(Collectors.toList()) : null);
        order.setCreatedAt(entity.getCreatedAt());
        order.setUpdatedAt(entity.getUpdatedAt());
        return order;
    }

    private OrderItemEntity toOrderItemEntity(OrderItem item) {
        return new OrderItemEntity(item.getId(), item.getProductId(), item.getQuantity(), item.getPrice());
    }

    private OrderItem toOrderItemDomain(OrderItemEntity entity) {
        return new OrderItem(entity.getId(), entity.getProductId(), entity.getQuantity(), entity.getPrice());
    }
}
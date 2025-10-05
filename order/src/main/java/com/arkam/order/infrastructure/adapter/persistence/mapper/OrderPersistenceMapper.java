package com.arkam.order.infrastructure.adapter.persistence.mapper;

import com.arkam.order.domain.model.Order;
import com.arkam.order.domain.model.OrderItem;
import com.arkam.order.infrastructure.adapter.persistence.entity.OrderEntity;
import com.arkam.order.infrastructure.adapter.persistence.entity.OrderItemEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class OrderPersistenceMapper {

    public OrderEntity toEntity(Order order) {
        OrderEntity entity = new OrderEntity();
        entity.setId(order.getId());
        entity.setUserId(order.getUserId());
        entity.setTotalAmount(order.getTotalAmount());
        entity.setStatus(order.getStatus());
        entity.setItems(order.getItems().stream()
                .map(item -> {
                    OrderItemEntity itemEntity = new OrderItemEntity();
                    itemEntity.setId(item.getId());
                    itemEntity.setProductId(item.getProductId());
                    itemEntity.setQuantity(item.getQuantity());
                    itemEntity.setPrice(item.getPrice());
                    itemEntity.setOrder(entity);
                    return itemEntity;
                })
                .collect(Collectors.toList()));
        entity.setCreatedAt(order.getCreatedAt());
        entity.setUpdatedAt(order.getUpdatedAt());
        return entity;
    }

    public Order toDomain(OrderEntity entity) {
        Order order = new Order();
        order.setId(entity.getId());
        order.setUserId(entity.getUserId());
        order.setTotalAmount(entity.getTotalAmount());
        order.setStatus(entity.getStatus());
        order.setItems(entity.getItems().stream()
                .map(itemEntity -> {
                    OrderItem item = new OrderItem();
                    item.setId(itemEntity.getId());
                    item.setProductId(itemEntity.getProductId());
                    item.setQuantity(itemEntity.getQuantity());
                    item.setPrice(itemEntity.getPrice());
                    return item;
                })
                .collect(Collectors.toList()));
        order.setCreatedAt(entity.getCreatedAt());
        order.setUpdatedAt(entity.getUpdatedAt());
        return order;
    }
}
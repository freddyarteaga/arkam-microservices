package com.arkam.order.infrastructure.adapter.persistence.mapper;

import com.arkam.order.domain.model.CartItem;
import com.arkam.order.infrastructure.adapter.persistence.entity.CartItemEntity;
import org.springframework.stereotype.Component;

@Component
public class CartItemPersistenceMapper {

    public CartItemEntity toEntity(CartItem cartItem) {
        CartItemEntity entity = new CartItemEntity();
        entity.setId(cartItem.getId());
        entity.setUserId(cartItem.getUserId());
        entity.setProductId(cartItem.getProductId());
        entity.setQuantity(cartItem.getQuantity());
        entity.setPrice(cartItem.getPrice());
        entity.setCreatedAt(cartItem.getCreatedAt());
        entity.setUpdatedAt(cartItem.getUpdatedAt());
        return entity;
    }

    public CartItem toDomain(CartItemEntity entity) {
        CartItem cartItem = new CartItem();
        cartItem.setId(entity.getId());
        cartItem.setUserId(entity.getUserId());
        cartItem.setProductId(entity.getProductId());
        cartItem.setQuantity(entity.getQuantity());
        cartItem.setPrice(entity.getPrice());
        cartItem.setCreatedAt(entity.getCreatedAt());
        cartItem.setUpdatedAt(entity.getUpdatedAt());
        return cartItem;
    }
}
package com.arkam.product.infrastructure.adapter.persistence.mapper;

import com.arkam.product.domain.model.Product;
import com.arkam.product.infrastructure.adapter.persistence.entity.ProductEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductPersistenceMapper {

    public ProductEntity toEntity(Product product) {
        ProductEntity entity = new ProductEntity();
        entity.setId(product.getId());
        entity.setName(product.getName());
        entity.setDescription(product.getDescription());
        entity.setPrice(product.getPrice());
        entity.setStockQuantity(product.getStockQuantity());
        entity.setCategory(product.getCategory());
        entity.setImageUrl(product.getImageUrl());
        entity.setActive(product.getActive());
        entity.setCreatedAt(product.getCreatedAt());
        entity.setUpdatedAt(product.getUpdatedAt());
        return entity;
    }

    public Product toDomain(ProductEntity entity) {
        Product product = new Product();
        product.setId(entity.getId());
        product.setName(entity.getName());
        product.setDescription(entity.getDescription());
        product.setPrice(entity.getPrice());
        product.setStockQuantity(entity.getStockQuantity());
        product.setCategory(entity.getCategory());
        product.setImageUrl(entity.getImageUrl());
        product.setActive(entity.getActive());
        product.setCreatedAt(entity.getCreatedAt());
        product.setUpdatedAt(entity.getUpdatedAt());
        return product;
    }
}
package com.arkam.product.infrastructure.adapter.persistence.mapper;

import com.arkam.product.domain.model.Product;
import com.arkam.product.infrastructure.adapter.persistence.entity.ProductEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ProductPersistenceMapper {

    public Product toDomain(ProductEntity entity) {
        return new Product(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            entity.getPrice(),
            entity.getStockQuantity(),
            entity.getCategory(),
            entity.getImageUrl(),
            entity.getActive(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    public ProductEntity toEntity(Product product) {
        ProductEntity entity = new ProductEntity();
        entity.setId(product.getId());
        entity.setName(product.getName());
        entity.setDescription(product.getDescription());
        entity.setPrice(product.getPrice());
        entity.setStockQuantity(product.getStockQuantity());
        entity.setCategory(product.getCategory());
        entity.setImageUrl(product.getImageUrl());
        entity.setActive(Optional.ofNullable(product.getActive()).orElse(Boolean.TRUE));
        // No establecemos createdAt ni updatedAt - se gestionan con anotaciones
        return entity;
    }
}

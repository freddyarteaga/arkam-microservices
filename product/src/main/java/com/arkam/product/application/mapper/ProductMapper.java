package com.arkam.product.application.mapper;

import com.arkam.product.application.dto.request.CreateProductRequestDto;
import com.arkam.product.application.dto.request.UpdateProductRequestDto;
import com.arkam.product.application.dto.response.ProductResponseDto;
import com.arkam.product.domain.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toProduct(CreateProductRequestDto dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStockQuantity(dto.getStockQuantity());
        product.setCategory(dto.getCategory());
        product.setImageUrl(dto.getImageUrl());
        product.setActive(true);
        return product;
    }

    public void updateProductFromDto(Product product, UpdateProductRequestDto dto) {
        if (dto.getName() != null) {
            product.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            product.setDescription(dto.getDescription());
        }
        if (dto.getPrice() != null) {
            product.setPrice(dto.getPrice());
        }
        if (dto.getStockQuantity() != null) {
            product.setStockQuantity(dto.getStockQuantity());
        }
        if (dto.getCategory() != null) {
            product.setCategory(dto.getCategory());
        }
        if (dto.getImageUrl() != null) {
            product.setImageUrl(dto.getImageUrl());
        }
        if (dto.getActive() != null) {
            product.setActive(dto.getActive());
        }
    }

    public ProductResponseDto toResponseDto(Product product) {
        return new ProductResponseDto(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getStockQuantity(),
            product.getCategory(),
            product.getImageUrl(),
            product.getActive(),
            product.getCreatedAt(),
            product.getUpdatedAt()
        );
    }
}

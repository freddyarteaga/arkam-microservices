package com.arkam.product.infrastructure.repository;

import com.arkam.product.application.port.ProductRepositoryPort;
import com.arkam.product.domain.Product;
import com.arkam.product.model.ProductEntity;
import com.arkam.product.repository.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepositoryPort {

    private final ProductJpaRepository productJpaRepository;

    private Product toDomain(ProductEntity entity) {
        // Mapea ProductEntity a Product
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

    private ProductEntity toEntity(Product product) {
        // Mapea Product a ProductEntity
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

    @Override
    public Product save(Product product) {
        ProductEntity entity = toEntity(product);
        ProductEntity saved = productJpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return productJpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Product> findByActiveTrue() {
        return productJpaRepository.findByActiveTrue().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Product> searchProducts(String keyword) {
        return productJpaRepository.searchProducts(keyword).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Product> findByIdAndActiveTrue(Long id) {
        return productJpaRepository.findByIdAndActiveTrue(id).map(this::toDomain);
    }
}
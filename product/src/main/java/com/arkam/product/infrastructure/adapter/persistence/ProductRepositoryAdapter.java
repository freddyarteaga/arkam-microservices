package com.arkam.product.infrastructure.adapter.persistence;

import com.arkam.product.application.port.out.ProductRepositoryPort;
import com.arkam.product.domain.model.Product;
import com.arkam.product.infrastructure.adapter.persistence.entity.ProductEntity;
import com.arkam.product.infrastructure.adapter.persistence.mapper.ProductPersistenceMapper;
import com.arkam.product.infrastructure.adapter.persistence.repository.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepositoryPort {

    private final ProductJpaRepository productJpaRepository;
    private final ProductPersistenceMapper mapper;

    @Override
    public Product save(Product product) {
        ProductEntity entity = mapper.toEntity(product);
        ProductEntity saved = productJpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return productJpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Product> findByActiveTrue() {
        return productJpaRepository.findByActiveTrue().stream()
               .map(mapper::toDomain)
               .collect(Collectors.toList());
    }

    @Override
    public List<Product> searchProducts(String keyword) {
        return productJpaRepository.findByNameContainingIgnoreCaseAndActiveTrue(keyword).stream()
               .map(mapper::toDomain)
               .collect(Collectors.toList());
    }
}

package com.arkam.product.infrastructure.adapter.out;

import com.arkam.product.application.port.out.ProductRepositoryPort;
import com.arkam.product.domain.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepositoryPort {

    private final ProductRepository productRepository;
    private final ProductPersistenceMapper mapper;

    @Override
    public Product save(Product product) {
        var entity = mapper.toEntity(product);
        entity = productRepository.save(entity);
        return mapper.toDomain(entity);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Product> findByActiveTrue() {
        return productRepository.findByActiveTrue().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> searchProducts(String keyword) {
        return productRepository.searchProducts(keyword).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Product> findByIdAndActiveTrue(Long id) {
        return productRepository.findByIdAndActiveTrue(id).map(mapper::toDomain);
    }
}
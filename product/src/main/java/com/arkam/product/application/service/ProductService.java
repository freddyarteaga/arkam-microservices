package com.arkam.product.application.service;

import com.arkam.product.application.port.ProductRepositoryPort;
import com.arkam.product.domain.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepositoryPort productRepository;

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> findByActiveTrue() {
        return productRepository.findByActiveTrue();
    }

    public List<Product> searchProducts(String keyword) {
        return productRepository.searchProducts(keyword);
    }

    public Optional<Product> findByIdAndActiveTrue(Long id) {
        return productRepository.findByIdAndActiveTrue(id);
    }
}
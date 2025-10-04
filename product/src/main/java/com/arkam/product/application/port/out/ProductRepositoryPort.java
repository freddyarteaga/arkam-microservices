package com.arkam.product.application.port.out;

import com.arkam.product.domain.model.Product;
import java.util.List;
import java.util.Optional;

public interface ProductRepositoryPort {
    Product save(Product product);
    Optional<Product> findById(Long id);
    List<Product> findByActiveTrue();
    List<Product> searchProducts(String keyword);
    Optional<Product> findByIdAndActiveTrue(Long id);
}
package com.arkam.product.application.service;

import com.arkam.product.application.dto.ProductRequest;
import com.arkam.product.application.dto.ProductResponse;
import com.arkam.product.application.port.in.*;
import com.arkam.product.application.port.out.ProductRepositoryPort;
import com.arkam.product.domain.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductApplicationService implements CreateProductUseCase, GetProductUseCase, GetAllProductsUseCase, UpdateProductUseCase, DeleteProductUseCase, SearchProductsUseCase {

    private final ProductRepositoryPort productRepository;

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        Product product = mapToDomain(request);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        Product savedProduct = productRepository.save(product);
        return mapToResponse(savedProduct);
    }

    @Override
    public Optional<ProductResponse> getProduct(String id) {
        return productRepository.findByIdAndActiveTrue(Long.valueOf(id)).map(this::mapToResponse);
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findByActiveTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ProductResponse> updateProduct(Long id, ProductRequest request) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    updateProductFromRequest(existingProduct, request);
                    existingProduct.setUpdatedAt(LocalDateTime.now());
                    Product savedProduct = productRepository.save(existingProduct);
                    return mapToResponse(savedProduct);
                });
    }

    @Override
    public boolean deleteProduct(Long id) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setActive(false);
                    productRepository.save(product);
                    return true;
                }).orElse(false);
    }

    @Override
    public List<ProductResponse> searchProducts(String keyword) {
        return productRepository.searchProducts(keyword).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private Product mapToDomain(ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(request.getCategory());
        product.setImageUrl(request.getImageUrl());
        return product;
    }

    private void updateProductFromRequest(Product product, ProductRequest request) {
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(request.getCategory());
        product.setImageUrl(request.getImageUrl());
    }

    private ProductResponse mapToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setStockQuantity(product.getStockQuantity());
        response.setCategory(product.getCategory());
        response.setImageUrl(product.getImageUrl());
        response.setActive(product.getActive());
        return response;
    }
}
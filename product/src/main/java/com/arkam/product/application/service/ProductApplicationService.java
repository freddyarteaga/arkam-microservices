package com.arkam.product.application.service;

import com.arkam.product.application.dto.ProductRequest;
import com.arkam.product.application.dto.ProductResponse;
import com.arkam.product.application.port.in.*;
import com.arkam.product.application.port.out.ProductRepositoryPort;
import com.arkam.product.domain.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProductApplicationService implements CreateProductUseCase, GetProductUseCase, GetAllProductsUseCase, UpdateProductUseCase, DeleteProductUseCase, SearchProductsUseCase {

    private final ProductRepositoryPort productRepository;

    @Override
    public Mono<ProductResponse> createProduct(ProductRequest request) {
        // Domain validation
        if (!isValidRequest(request)) {
            return Mono.error(new IllegalArgumentException("se requiere nombre para el producto"));
        }

        Product product = mapToDomain(request);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        return productRepository.save(product)
                .map(this::mapToResponse);
    }

    @Override
    public Mono<ProductResponse> getProduct(Long id) {
        return productRepository.findById(id)
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()))
                .map(this::mapToResponse);
    }

    @Override
    public Flux<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .map(this::mapToResponse);
    }

    @Override
    public Mono<Boolean> updateProduct(Long id, ProductRequest request) {
        return productRepository.findById(id)
                .flatMap(optional -> optional.map(existing -> {
                    updateProductFromRequest(existing, request);
                    existing.setUpdatedAt(LocalDateTime.now());
                    return productRepository.save(existing)
                            .then(Mono.just(true));
                }).orElse(Mono.just(false)));
    }

    @Override
    public Mono<Boolean> deleteProduct(Long id) {
        return productRepository.findById(id)
                .flatMap(optional -> optional.map(product -> {
                    product.setActive(false);
                    return productRepository.save(product)
                            .then(Mono.just(true));
                }).orElse(Mono.just(false)));
    }

    @Override
    public Flux<ProductResponse> searchProducts(String keyword) {
        return productRepository.searchProducts(keyword)
                .map(this::mapToResponse);
    }

    private boolean isValidRequest(ProductRequest request) {
        return request != null && request.getName() != null && !request.getName().trim().isEmpty();
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
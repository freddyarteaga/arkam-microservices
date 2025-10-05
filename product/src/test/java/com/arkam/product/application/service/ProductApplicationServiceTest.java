package com.arkam.product.application.service;

import com.arkam.product.application.dto.ProductRequest;
import com.arkam.product.application.dto.ProductResponse;
import com.arkam.product.application.port.out.ProductRepositoryPort;
import com.arkam.product.domain.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ProductApplicationServiceTest {

    @Mock
    private ProductRepositoryPort productRepository;

    @InjectMocks
    private ProductApplicationService productApplicationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateProduct() {
        ProductRequest request = new ProductRequest();
        request.setName("Test Product");
        request.setPrice(BigDecimal.valueOf(100.0));

        Product savedProduct = new Product();
        savedProduct.setId(1L);
        savedProduct.setName("Test Product");
        savedProduct.setPrice(BigDecimal.valueOf(100.0));
        savedProduct.setActive(true);

        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(savedProduct));

        Mono<ProductResponse> result = productApplicationService.createProduct(request);

        StepVerifier.create(result)
                .expectNextMatches(response -> response.getName().equals("Test Product"))
                .verifyComplete();
    }

    @Test
    void testGetProduct() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");

        when(productRepository.findById(1L)).thenReturn(Mono.just(Optional.of(product)));

        Mono<ProductResponse> result = productApplicationService.getProduct(1L);

        StepVerifier.create(result)
                .expectNextMatches(response -> response.getId().equals(1L))
                .verifyComplete();
    }

    @Test
    void testGetAllProducts() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");

        when(productRepository.findAll()).thenReturn(Flux.just(product));

        Flux<ProductResponse> result = productApplicationService.getAllProducts();

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void testUpdateProduct() {
        ProductRequest request = new ProductRequest();
        request.setName("Updated Product");

        Product existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setName("Old Product");

        when(productRepository.findById(1L)).thenReturn(Mono.just(Optional.of(existingProduct)));
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(existingProduct));

        Mono<Boolean> result = productApplicationService.updateProduct(1L, request);

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void testDeleteProduct() {
        Product product = new Product();
        product.setId(1L);

        when(productRepository.findById(1L)).thenReturn(Mono.just(Optional.of(product)));
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(product));

        Mono<Boolean> result = productApplicationService.deleteProduct(1L);

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void testSearchProducts() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");

        when(productRepository.searchProducts("test")).thenReturn(Flux.just(product));

        Flux<ProductResponse> result = productApplicationService.searchProducts("test");

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }
}
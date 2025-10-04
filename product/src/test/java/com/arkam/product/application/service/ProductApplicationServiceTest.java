package com.arkam.product.application.service;

import com.arkam.product.application.dto.ProductRequest;
import com.arkam.product.application.dto.ProductResponse;
import com.arkam.product.application.port.out.ProductRepositoryPort;
import com.arkam.product.domain.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductApplicationServiceTest {

    @Mock
    private ProductRepositoryPort productRepository;

    @InjectMocks
    private ProductApplicationService productApplicationService;

    private ProductRequest productRequest;
    private Product product;
    private ProductResponse productResponse;

    @BeforeEach
    void setUp() {
        productRequest = new ProductRequest();
        productRequest.setName("Test Product");
        productRequest.setDescription("Test Description");
        productRequest.setPrice(BigDecimal.valueOf(99.99));
        productRequest.setStockQuantity(10);
        productRequest.setCategory("Electronics");
        productRequest.setImageUrl("http://example.com/image.jpg");

        product = new Product();
        product.setId("prod123");
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(BigDecimal.valueOf(99.99));
        product.setStockQuantity(10);
        product.setCategory("Electronics");
        product.setImageUrl("http://example.com/image.jpg");
        product.setActive(true);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        productResponse = new ProductResponse();
        productResponse.setId("prod123");
        productResponse.setName("Test Product");
        productResponse.setDescription("Test Description");
        productResponse.setPrice(BigDecimal.valueOf(99.99));
        productResponse.setStockQuantity(10);
        productResponse.setCategory("Electronics");
        productResponse.setImageUrl("http://example.com/image.jpg");
        productResponse.setActive(true);
    }

    @Test
    void createProduct_Success() {
        // Given
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(product));

        // When
        Mono<ProductResponse> result = productApplicationService.createProduct(productRequest);

        // Then
        StepVerifier.create(result)
                .expectNext(productResponse)
                .verifyComplete();
    }

    @Test
    void getProduct_Success() {
        // Given
        when(productRepository.findByIdAndActiveTrue("prod123")).thenReturn(Mono.just(product));

        // When
        Mono<ProductResponse> result = productApplicationService.getProduct("prod123");

        // Then
        StepVerifier.create(result)
                .expectNext(productResponse)
                .verifyComplete();
    }

    @Test
    void getProduct_NotFound() {
        // Given
        when(productRepository.findByIdAndActiveTrue("nonexistent")).thenReturn(Mono.empty());

        // When
        Mono<ProductResponse> result = productApplicationService.getProduct("nonexistent");

        // Then
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void getAllProducts_Success() {
        // Given
        Product product2 = new Product();
        product2.setId("prod456");
        product2.setName("Test Product 2");
        product2.setDescription("Test Description 2");
        product2.setPrice(BigDecimal.valueOf(49.99));
        product2.setStockQuantity(5);
        product2.setCategory("Books");
        product2.setImageUrl("http://example.com/image2.jpg");
        product2.setActive(true);

        ProductResponse productResponse2 = new ProductResponse();
        productResponse2.setId("prod456");
        productResponse2.setName("Test Product 2");
        productResponse2.setDescription("Test Description 2");
        productResponse2.setPrice(BigDecimal.valueOf(49.99));
        productResponse2.setStockQuantity(5);
        productResponse2.setCategory("Books");
        productResponse2.setImageUrl("http://example.com/image2.jpg");
        productResponse2.setActive(true);

        when(productRepository.findByActiveTrue()).thenReturn(Flux.just(product, product2));

        // When
        Flux<ProductResponse> result = productApplicationService.getAllProducts();

        // Then
        StepVerifier.create(result)
                .expectNext(productResponse)
                .expectNext(productResponse2)
                .verifyComplete();
    }

    @Test
    void updateProduct_Success() {
        // Given
        ProductRequest updateRequest = new ProductRequest();
        updateRequest.setName("Updated Product");
        updateRequest.setDescription("Updated Description");
        updateRequest.setPrice(BigDecimal.valueOf(149.99));
        updateRequest.setStockQuantity(20);
        updateRequest.setCategory("Electronics");
        updateRequest.setImageUrl("http://example.com/updated.jpg");

        Product updatedProduct = new Product();
        updatedProduct.setId("prod123");
        updatedProduct.setName("Updated Product");
        updatedProduct.setDescription("Updated Description");
        updatedProduct.setPrice(BigDecimal.valueOf(149.99));
        updatedProduct.setStockQuantity(20);
        updatedProduct.setCategory("Electronics");
        updatedProduct.setImageUrl("http://example.com/updated.jpg");
        updatedProduct.setActive(true);

        ProductResponse updatedResponse = new ProductResponse();
        updatedResponse.setId("prod123");
        updatedResponse.setName("Updated Product");
        updatedResponse.setDescription("Updated Description");
        updatedResponse.setPrice(BigDecimal.valueOf(149.99));
        updatedResponse.setStockQuantity(20);
        updatedResponse.setCategory("Electronics");
        updatedResponse.setImageUrl("http://example.com/updated.jpg");
        updatedResponse.setActive(true);

        when(productRepository.findById("prod123")).thenReturn(Mono.just(product));
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(updatedProduct));

        // When
        Mono<ProductResponse> result = productApplicationService.updateProduct("prod123", updateRequest);

        // Then
        StepVerifier.create(result)
                .expectNext(updatedResponse)
                .verifyComplete();
    }

    @Test
    void updateProduct_NotFound() {
        // Given
        ProductRequest updateRequest = new ProductRequest();
        updateRequest.setName("Updated Product");

        when(productRepository.findById("nonexistent")).thenReturn(Mono.empty());

        // When
        Mono<ProductResponse> result = productApplicationService.updateProduct("nonexistent", updateRequest);

        // Then
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void deleteProduct_Success() {
        // Given
        when(productRepository.findById("prod123")).thenReturn(Mono.just(product));
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(product));

        // When
        Mono<Boolean> result = productApplicationService.deleteProduct("prod123");

        // Then
        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void deleteProduct_NotFound() {
        // Given
        when(productRepository.findById("nonexistent")).thenReturn(Mono.empty());

        // When
        Mono<Boolean> result = productApplicationService.deleteProduct("nonexistent");

        // Then
        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void searchProducts_Success() {
        // Given
        when(productRepository.searchProducts("test")).thenReturn(Flux.just(product));

        // When
        Flux<ProductResponse> result = productApplicationService.searchProducts("test");

        // Then
        StepVerifier.create(result)
                .expectNext(productResponse)
                .verifyComplete();
    }
}
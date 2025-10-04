package com.arkam.product.infrastructure.adapter.out;

import com.arkam.product.application.port.out.ProductRepositoryPort;
import com.arkam.product.domain.model.Product;
import com.arkam.product.infrastructure.ProductEntity;
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
class ProductRepositoryAdapterTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductPersistenceMapper mapper;

    @InjectMocks
    private ProductRepositoryAdapter productRepositoryAdapter;

    private Product product;
    private ProductEntity productEntity;

    @BeforeEach
    void setUp() {
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

        productEntity = new ProductEntity();
        productEntity.setId("prod123");
        productEntity.setName("Test Product");
        productEntity.setDescription("Test Description");
        productEntity.setPrice(BigDecimal.valueOf(99.99));
        productEntity.setStockQuantity(10);
        productEntity.setCategory("Electronics");
        productEntity.setImageUrl("http://example.com/image.jpg");
        productEntity.setActive(true);
        productEntity.setCreatedAt(LocalDateTime.now());
        productEntity.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void save_Success() {
        // Given
        when(mapper.toEntity(any(Product.class))).thenReturn(productEntity);
        when(productRepository.save(any(ProductEntity.class))).thenReturn(Mono.just(productEntity));
        when(mapper.toDomain(any(ProductEntity.class))).thenReturn(product);

        // When
        Mono<Product> result = productRepositoryAdapter.save(product);

        // Then
        StepVerifier.create(result)
                .expectNext(product)
                .verifyComplete();
    }

    @Test
    void findById_Success() {
        // Given
        when(productRepository.findById("prod123")).thenReturn(Mono.just(productEntity));
        when(mapper.toDomain(productEntity)).thenReturn(product);

        // When
        Mono<Product> result = productRepositoryAdapter.findById("prod123");

        // Then
        StepVerifier.create(result)
                .expectNext(product)
                .verifyComplete();
    }

    @Test
    void findById_NotFound() {
        // Given
        when(productRepository.findById("nonexistent")).thenReturn(Mono.empty());

        // When
        Mono<Product> result = productRepositoryAdapter.findById("nonexistent");

        // Then
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void findByIdAndActiveTrue_Success() {
        // Given
        when(productRepository.findByIdAndActiveTrue("prod123")).thenReturn(Mono.just(productEntity));
        when(mapper.toDomain(productEntity)).thenReturn(product);

        // When
        Mono<Product> result = ((ProductRepositoryPort) productRepositoryAdapter).findByIdAndActiveTrue("prod123");

        // Then
        StepVerifier.create(result)
                .expectNext(product)
                .verifyComplete();
    }

    @Test
    void findByActiveTrue_Success() {
        // Given
        when(productRepository.findByActiveTrue()).thenReturn(Flux.just(productEntity));
        when(mapper.toDomain(productEntity)).thenReturn(product);

        // When
        Flux<Product> result = ((ProductRepositoryPort) productRepositoryAdapter).findByActiveTrue();

        // Then
        StepVerifier.create(result)
                .expectNext(product)
                .verifyComplete();
    }

    @Test
    void searchProducts_Success() {
        // Given
        when(productRepository.findByActiveTrueAndStockQuantityGreaterThanAndNameContainingIgnoreCase(0, "test")).thenReturn(Flux.just(productEntity));
        when(mapper.toDomain(productEntity)).thenReturn(product);

        // When
        Flux<Product> result = ((ProductRepositoryPort) productRepositoryAdapter).searchProducts("test");

        // Then
        StepVerifier.create(result)
                .expectNext(product)
                .verifyComplete();
    }
}
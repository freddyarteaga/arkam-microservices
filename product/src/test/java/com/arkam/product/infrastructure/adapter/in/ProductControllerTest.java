package com.arkam.product.infrastructure.adapter.in;

import com.arkam.product.application.dto.ProductRequest;
import com.arkam.product.application.dto.ProductResponse;
import com.arkam.product.application.port.in.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CreateProductUseCase createProductUseCase;

    @MockBean
    private GetProductUseCase getProductUseCase;

    @MockBean
    private GetAllProductsUseCase getAllProductsUseCase;

    @MockBean
    private UpdateProductUseCase updateProductUseCase;

    @MockBean
    private DeleteProductUseCase deleteProductUseCase;

    @MockBean
    private SearchProductsUseCase searchProductsUseCase;

    private ProductRequest productRequest;
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
        when(createProductUseCase.createProduct(any(ProductRequest.class)))
                .thenReturn(Mono.just(productResponse));

        // When & Then
        webTestClient.post()
                .uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(productRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ProductResponse.class)
                .isEqualTo(productResponse);
    }

    @Test
    void createProduct_InvalidRequest() {
        // Given
        ProductRequest invalidRequest = new ProductRequest();
        when(createProductUseCase.createProduct(any(ProductRequest.class)))
                .thenReturn(Mono.error(new IllegalArgumentException("Invalid product request")));

        // When & Then
        webTestClient.post()
                .uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getProductById_Success() {
        // Given
        when(getProductUseCase.getProduct("prod123"))
                .thenReturn(Mono.just(productResponse));

        // When & Then
        webTestClient.get()
                .uri("/api/products/prod123")
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductResponse.class)
                .isEqualTo(productResponse);
    }

    @Test
    void getProductById_NotFound() {
        // Given
        when(getProductUseCase.getProduct("nonexistent"))
                .thenReturn(Mono.empty());

        // When & Then
        webTestClient.get()
                .uri("/api/products/nonexistent")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getProducts_Success() {
        // Given
        ProductResponse productResponse2 = new ProductResponse();
        productResponse2.setId("prod456");
        productResponse2.setName("Test Product 2");
        productResponse2.setDescription("Test Description 2");
        productResponse2.setPrice(BigDecimal.valueOf(49.99));
        productResponse2.setStockQuantity(5);
        productResponse2.setCategory("Books");
        productResponse2.setImageUrl("http://example.com/image2.jpg");
        productResponse2.setActive(true);

        when(getAllProductsUseCase.getAllProducts())
                .thenReturn(Flux.just(productResponse, productResponse2));

        // When & Then
        webTestClient.get()
                .uri("/api/products")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductResponse.class)
                .hasSize(2)
                .contains(productResponse, productResponse2);
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

        ProductResponse updatedResponse = new ProductResponse();
        updatedResponse.setId("prod123");
        updatedResponse.setName("Updated Product");
        updatedResponse.setDescription("Updated Description");
        updatedResponse.setPrice(BigDecimal.valueOf(149.99));
        updatedResponse.setStockQuantity(20);
        updatedResponse.setCategory("Electronics");
        updatedResponse.setImageUrl("http://example.com/updated.jpg");
        updatedResponse.setActive(true);

        when(updateProductUseCase.updateProduct(anyString(), any(ProductRequest.class)))
                .thenReturn(Mono.just(updatedResponse));

        // When & Then
        webTestClient.put()
                .uri("/api/products/prod123")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductResponse.class)
                .isEqualTo(updatedResponse);
    }

    @Test
    void updateProduct_NotFound() {
        // Given
        ProductRequest updateRequest = new ProductRequest();
        updateRequest.setName("Updated Product");

        when(updateProductUseCase.updateProduct(anyString(), any(ProductRequest.class)))
                .thenReturn(Mono.empty());

        // When & Then
        webTestClient.put()
                .uri("/api/products/nonexistent")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void deleteProduct_Success() {
        // Given
        when(deleteProductUseCase.deleteProduct("prod123"))
                .thenReturn(Mono.just(true));

        // When & Then
        webTestClient.delete()
                .uri("/api/products/prod123")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void deleteProduct_NotFound() {
        // Given
        when(deleteProductUseCase.deleteProduct("nonexistent"))
                .thenReturn(Mono.just(false));

        // When & Then
        webTestClient.delete()
                .uri("/api/products/nonexistent")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void searchProducts_Success() {
        // Given
        when(searchProductsUseCase.searchProducts("test"))
                .thenReturn(Flux.just(productResponse));

        // When & Then
        webTestClient.get()
                .uri("/api/products/search?keyword=test")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductResponse.class)
                .hasSize(1)
                .contains(productResponse);
    }
}
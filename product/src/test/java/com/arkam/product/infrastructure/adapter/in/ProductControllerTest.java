package com.arkam.product.infrastructure.adapter.in;

import com.arkam.product.application.dto.ProductRequest;
import com.arkam.product.application.dto.ProductResponse;
import com.arkam.product.application.port.in.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
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
        productRequest.setPrice(BigDecimal.valueOf(100.0));

        productResponse = new ProductResponse();
        productResponse.setId(1L);
        productResponse.setName("Test Product");
        productResponse.setPrice(BigDecimal.valueOf(100.0));
        productResponse.setActive(true);
    }

    @Test
    void testCreateProduct() {
        when(createProductUseCase.createProduct(any(ProductRequest.class))).thenReturn(Mono.just(productResponse));

        webTestClient.post()
                .uri("/api/products")
                .bodyValue(productRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ProductResponse.class)
                .isEqualTo(productResponse);
    }

    @Test
    void testGetAllProducts() {
        when(getAllProductsUseCase.getAllProducts()).thenReturn(Flux.just(productResponse));

        webTestClient.get()
                .uri("/api/products")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductResponse.class)
                .hasSize(1);
    }

    @Test
    void testGetProductById() {
        when(getProductUseCase.getProduct(1L)).thenReturn(Mono.just(productResponse));

        webTestClient.get()
                .uri("/api/products/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductResponse.class)
                .isEqualTo(productResponse);
    }

    @Test
    void testUpdateProduct() {
        when(updateProductUseCase.updateProduct(1L, productRequest)).thenReturn(Mono.just(true));

        webTestClient.put()
                .uri("/api/products/1")
                .bodyValue(productRequest)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testDeleteProduct() {
        when(deleteProductUseCase.deleteProduct(1L)).thenReturn(Mono.just(true));

        webTestClient.delete()
                .uri("/api/products/1")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void testSearchProducts() {
        when(searchProductsUseCase.searchProducts("test")).thenReturn(Flux.just(productResponse));

        webTestClient.get()
                .uri("/api/products/search?keyword=test")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductResponse.class)
                .hasSize(1);
    }
}
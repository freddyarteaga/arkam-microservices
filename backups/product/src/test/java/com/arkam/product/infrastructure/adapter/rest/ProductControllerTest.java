package com.arkam.product.infrastructure.adapter.rest;

import com.arkam.product.application.dto.request.CreateProductRequestDto;
import com.arkam.product.application.dto.request.UpdateProductRequestDto;
import com.arkam.product.application.dto.response.ProductResponseDto;
import com.arkam.product.application.port.in.CreateProductUseCase;
import com.arkam.product.application.port.in.DeleteProductUseCase;
import com.arkam.product.application.port.in.GetProductUseCase;
import com.arkam.product.application.port.in.UpdateProductUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private CreateProductUseCase createProductUseCase;

    @Mock
    private GetProductUseCase getProductUseCase;

    @Mock
    private UpdateProductUseCase updateProductUseCase;

    @Mock
    private DeleteProductUseCase deleteProductUseCase;

    @InjectMocks
    private ProductController productController;

    private ProductResponseDto productResponseDto;
    private CreateProductRequestDto createRequestDto;
    private UpdateProductRequestDto updateRequestDto;

    @BeforeEach
    void setUp() {
        productResponseDto = new ProductResponseDto();
        productResponseDto.setId(1L);
        productResponseDto.setName("Test Product");
        productResponseDto.setDescription("Test Description");
        productResponseDto.setPrice(new BigDecimal("99.99"));
        productResponseDto.setStockQuantity(100);
        productResponseDto.setActive(true);

        createRequestDto = new CreateProductRequestDto();
        createRequestDto.setName("Test Product");
        createRequestDto.setDescription("Test Description");
        createRequestDto.setPrice(new BigDecimal("99.99"));
        createRequestDto.setStockQuantity(100);

        updateRequestDto = new UpdateProductRequestDto();
        updateRequestDto.setName("Updated Product");
        updateRequestDto.setDescription("Updated Description");
        updateRequestDto.setPrice(new BigDecimal("149.99"));
        updateRequestDto.setStockQuantity(50);
    }

    @Test
    void createProduct_WithValidRequest_ShouldReturnCreatedResponse() {
        // Given
        when(createProductUseCase.createProduct(createRequestDto)).thenReturn(productResponseDto);

        // When
        ResponseEntity<ProductResponseDto> response = productController.createProduct(createRequestDto);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(productResponseDto, response.getBody());
        verify(createProductUseCase).createProduct(createRequestDto);
    }

    @Test
    void getAllProducts_ShouldReturnOkResponse() {
        // Given
        List<ProductResponseDto> products = Arrays.asList(productResponseDto);
        when(getProductUseCase.findAllActiveProducts()).thenReturn(products);

        // When
        ResponseEntity<List<ProductResponseDto>> response = productController.getAllProducts();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(productResponseDto, response.getBody().get(0));
        verify(getProductUseCase).findAllActiveProducts();
    }

    @Test
    void getAllProducts_WithEmptyList_ShouldReturnEmptyList() {
        // Given
        when(getProductUseCase.findAllActiveProducts()).thenReturn(Collections.emptyList());

        // When
        ResponseEntity<List<ProductResponseDto>> response = productController.getAllProducts();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(getProductUseCase).findAllActiveProducts();
    }

    @Test
    void getProductById_WithValidId_ShouldReturnProduct() {
        // Given
        Long productId = 1L;
        when(getProductUseCase.findProductById(productId)).thenReturn(productResponseDto);

        // When
        ResponseEntity<ProductResponseDto> response = productController.getProductById(productId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(productResponseDto, response.getBody());
        verify(getProductUseCase).findProductById(productId);
    }

    @Test
    void searchProducts_WithValidKeyword_ShouldReturnMatchingProducts() {
        // Given
        String keyword = "test";
        List<ProductResponseDto> products = Arrays.asList(productResponseDto);
        when(getProductUseCase.searchProducts(keyword)).thenReturn(products);

        // When
        ResponseEntity<List<ProductResponseDto>> response = productController.searchProducts(keyword);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(productResponseDto, response.getBody().get(0));
        verify(getProductUseCase).searchProducts(keyword);
    }

    @Test
    void searchProducts_WithNoMatches_ShouldReturnEmptyList() {
        // Given
        String keyword = "nonexistent";
        when(getProductUseCase.searchProducts(keyword)).thenReturn(Collections.emptyList());

        // When
        ResponseEntity<List<ProductResponseDto>> response = productController.searchProducts(keyword);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(getProductUseCase).searchProducts(keyword);
    }

    @Test
    void updateProduct_WithValidId_ShouldReturnUpdatedProduct() {
        // Given
        Long productId = 1L;
        when(updateProductUseCase.updateProduct(productId, updateRequestDto)).thenReturn(productResponseDto);

        // When
        ResponseEntity<ProductResponseDto> response = productController.updateProduct(productId, updateRequestDto);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(productResponseDto, response.getBody());
        verify(updateProductUseCase).updateProduct(productId, updateRequestDto);
    }

    @Test
    void deleteProduct_WithValidId_ShouldReturnNoContent() {
        // Given
        Long productId = 1L;
        doNothing().when(deleteProductUseCase).deleteProduct(productId);

        // When
        ResponseEntity<Void> response = productController.deleteProduct(productId);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(deleteProductUseCase).deleteProduct(productId);
    }
}

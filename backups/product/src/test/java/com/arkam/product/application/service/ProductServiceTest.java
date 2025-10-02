package com.arkam.product.application.service;

import com.arkam.product.application.dto.request.CreateProductRequestDto;
import com.arkam.product.application.dto.request.UpdateProductRequestDto;
import com.arkam.product.application.dto.response.ProductResponseDto;
import com.arkam.product.application.mapper.ProductMapper;
import com.arkam.product.application.port.out.ProductRepositoryPort;
import com.arkam.product.domain.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepositoryPort productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductResponseDto productResponseDto;
    private CreateProductRequestDto createRequestDto;
    private UpdateProductRequestDto updateRequestDto;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(new BigDecimal("99.99"));
        product.setStockQuantity(100);
        product.setActive(true);

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
    void createProduct_WithValidRequest_ShouldReturnProductResponse() {
        // Given
        when(productMapper.toProduct(createRequestDto)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toResponseDto(product)).thenReturn(productResponseDto);

        // When
        ProductResponseDto result = productService.createProduct(createRequestDto);

        // Then
        assertNotNull(result);
        assertEquals(productResponseDto.getId(), result.getId());
        assertEquals(productResponseDto.getName(), result.getName());
        assertEquals(productResponseDto.getPrice(), result.getPrice());

        verify(productMapper).toProduct(createRequestDto);
        verify(productRepository).save(product);
        verify(productMapper).toResponseDto(product);
    }

    @Test
    void updateProduct_WithValidId_ShouldReturnUpdatedProduct() {
        // Given
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toResponseDto(product)).thenReturn(productResponseDto);

        // When
        ProductResponseDto result = productService.updateProduct(productId, updateRequestDto);

        // Then
        assertNotNull(result);
        verify(productRepository).findById(productId);
        verify(productMapper).updateProductFromDto(product, updateRequestDto);
        verify(productRepository).save(product);
        verify(productMapper).toResponseDto(product);
    }

    @Test
    void updateProduct_WithNonExistentId_ShouldThrowException() {
        // Given
        Long productId = 999L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> 
            productService.updateProduct(productId, updateRequestDto));
        
        verify(productRepository).findById(productId);
        verify(productMapper, never()).updateProductFromDto(any(), any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void findAllActiveProducts_ShouldReturnActiveProducts() {
        // Given
        List<Product> products = Arrays.asList(product);
        when(productRepository.findByActiveTrue()).thenReturn(products);
        when(productMapper.toResponseDto(product)).thenReturn(productResponseDto);

        // When
        List<ProductResponseDto> result = productService.findAllActiveProducts();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(productResponseDto, result.get(0));

        verify(productRepository).findByActiveTrue();
        verify(productMapper).toResponseDto(product);
    }

    @Test
    void findAllActiveProducts_WithNoProducts_ShouldReturnEmptyList() {
        // Given
        when(productRepository.findByActiveTrue()).thenReturn(Collections.emptyList());

        // When
        List<ProductResponseDto> result = productService.findAllActiveProducts();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productRepository).findByActiveTrue();
    }

    @Test
    void findProductById_WithValidId_ShouldReturnProduct() {
        // Given
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productMapper.toResponseDto(product)).thenReturn(productResponseDto);

        // When
        ProductResponseDto result = productService.findProductById(productId);

        // Then
        assertNotNull(result);
        assertEquals(productResponseDto, result);
        verify(productRepository).findById(productId);
        verify(productMapper).toResponseDto(product);
    }

    @Test
    void findProductById_WithNonExistentId_ShouldThrowException() {
        // Given
        Long productId = 999L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> 
            productService.findProductById(productId));
        
        verify(productRepository).findById(productId);
        verify(productMapper, never()).toResponseDto(any());
    }

    @Test
    void searchProducts_WithValidKeyword_ShouldReturnMatchingProducts() {
        // Given
        String keyword = "test";
        List<Product> products = Collections.singletonList(product);
        when(productRepository.searchProducts(keyword)).thenReturn(products);
        when(productMapper.toResponseDto(product)).thenReturn(productResponseDto);

        // When
        List<ProductResponseDto> result = productService.searchProducts(keyword);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(productResponseDto, result.getFirst());

        verify(productRepository).searchProducts(keyword);
        verify(productMapper).toResponseDto(product);
    }

    @Test
    void searchProducts_WithNoMatches_ShouldReturnEmptyList() {
        // Given
        String keyword = "nonexistent";
        when(productRepository.searchProducts(keyword)).thenReturn(Collections.emptyList());

        // When
        List<ProductResponseDto> result = productService.searchProducts(keyword);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productRepository).searchProducts(keyword);
    }

    @Test
    void deleteProduct_WithValidId_ShouldDeactivateProduct() {
        // Given
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        // When
        productService.deleteProduct(productId);

        // Then
        assertFalse(product.getActive());
        verify(productRepository).findById(productId);
        verify(productRepository).save(product);
    }

    @Test
    void deleteProduct_WithNonExistentId_ShouldThrowException() {
        // Given
        Long productId = 999L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> 
            productService.deleteProduct(productId));
        
        verify(productRepository).findById(productId);
        verify(productRepository, never()).save(any());
    }
}

package com.arkam.product.application.service;

import com.arkam.product.application.dto.request.CreateProductRequestDto;
import com.arkam.product.application.dto.request.UpdateProductRequestDto;
import com.arkam.product.application.dto.response.ProductResponseDto;
import com.arkam.product.application.mapper.ProductMapper;
import com.arkam.product.application.port.in.CreateProductUseCase;
import com.arkam.product.application.port.in.DeleteProductUseCase;
import com.arkam.product.application.port.in.GetProductUseCase;
import com.arkam.product.application.port.in.UpdateProductUseCase;
import com.arkam.product.application.port.out.ProductRepositoryPort;
import com.arkam.product.domain.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService implements CreateProductUseCase, GetProductUseCase, UpdateProductUseCase, DeleteProductUseCase {
    private final ProductRepositoryPort productRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductResponseDto createProduct(CreateProductRequestDto requestDto) {
        Product product = productMapper.toProduct(requestDto);
        Product savedProduct = productRepository.save(product);
        return productMapper.toResponseDto(savedProduct);
    }

    @Override
    public ProductResponseDto updateProduct(Long id, UpdateProductRequestDto requestDto) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        productMapper.updateProductFromDto(product, requestDto);
        Product updatedProduct = productRepository.save(product);
        return productMapper.toResponseDto(updatedProduct);
    }

    @Override
    public List<ProductResponseDto> findAllActiveProducts() {
        return productRepository.findByActiveTrue().stream()
            .map(productMapper::toResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    public ProductResponseDto findProductById(Long id) {
        return productRepository.findById(id)
            .map(productMapper::toResponseDto)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }

    @Override
    public List<ProductResponseDto> searchProducts(String keyword) {
        return productRepository.searchProducts(keyword).stream()
            .map(productMapper::toResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        product.setActive(false);
        productRepository.save(product);
    }
}

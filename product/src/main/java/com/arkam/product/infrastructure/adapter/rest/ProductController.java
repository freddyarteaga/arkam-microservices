package com.arkam.product.infrastructure.adapter.rest;

import com.arkam.product.application.dto.request.CreateProductRequestDto;
import com.arkam.product.application.dto.request.UpdateProductRequestDto;
import com.arkam.product.application.dto.response.ProductResponseDto;
import com.arkam.product.application.port.in.CreateProductUseCase;
import com.arkam.product.application.port.in.DeleteProductUseCase;
import com.arkam.product.application.port.in.GetProductUseCase;
import com.arkam.product.application.port.in.UpdateProductUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final CreateProductUseCase createProductUseCase;
    private final GetProductUseCase getProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final DeleteProductUseCase deleteProductUseCase;

    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@Valid @RequestBody CreateProductRequestDto requestDto) {
        ProductResponseDto response = createProductUseCase.createProduct(requestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        List<ProductResponseDto> products = getProductUseCase.findAllActiveProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long id) {
        ProductResponseDto product = getProductUseCase.findProductById(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDto>> searchProducts(@RequestParam String keyword) {
        List<ProductResponseDto> products = getProductUseCase.searchProducts(keyword);
        return ResponseEntity.ok(products);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequestDto requestDto) {
        ProductResponseDto updatedProduct = updateProductUseCase.updateProduct(id, requestDto);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        deleteProductUseCase.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}

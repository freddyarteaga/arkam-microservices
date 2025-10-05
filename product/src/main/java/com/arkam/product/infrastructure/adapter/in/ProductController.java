package com.arkam.product.infrastructure.adapter.in;

import com.arkam.product.application.dto.ProductRequest;
import com.arkam.product.application.dto.ProductResponse;
import com.arkam.product.application.port.in.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final CreateProductUseCase createProductUseCase;
    private final GetProductUseCase getProductUseCase;
    private final GetAllProductsUseCase getAllProductsUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final DeleteProductUseCase deleteProductUseCase;
    private final SearchProductsUseCase searchProductsUseCase;

    @GetMapping("/simulate")
    public Mono<String> simulateFailure(@RequestParam(defaultValue = "false") boolean fail) {
        if (fail) {
            throw new RuntimeException("simulación de falla para testing");
        }
        return Mono.just("Servicio de productos funcionando correctamente");
    }

    @PostMapping
    public Mono<ResponseEntity<ProductResponse>> createProduct(@RequestBody ProductRequest productRequest) {
        return createProductUseCase.createProduct(productRequest)
                .map(product -> ResponseEntity.status(HttpStatus.CREATED).body(product))
                .onErrorResume(IllegalArgumentException.class,
                        ex -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @GetMapping
    public Flux<ProductResponse> getProducts() {
        return getAllProductsUseCase.getAllProducts();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ProductResponse>> getProductById(@PathVariable Long id) {
        return getProductUseCase.getProduct(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<String>> updateProduct(@PathVariable Long id, @RequestBody ProductRequest productRequest) {
        return updateProductUseCase.updateProduct(id, productRequest)
                .flatMap(updated -> updated ?
                        Mono.just(ResponseEntity.ok("Producto actualizado correctamente")) :
                        Mono.just(ResponseEntity.notFound().build()));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteProduct(@PathVariable Long id) {
        return deleteProductUseCase.deleteProduct(id)
                .flatMap(deleted -> deleted ? Mono.just(ResponseEntity.noContent().build()) : Mono.just(ResponseEntity.notFound().build()));
    }

    @GetMapping("/search")
    public Flux<ProductResponse> searchProducts(@RequestParam String keyword) {
        return searchProductsUseCase.searchProducts(keyword);
    }
}
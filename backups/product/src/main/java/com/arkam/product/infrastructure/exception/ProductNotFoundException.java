package com.arkam.product.infrastructure.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String message) {
        super(message);
    }

    public ProductNotFoundException(Long id) {
        super("Producto no encontrado con id: " + id);
    }
}

package com.arkam.product.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    void testIsAvailable_WhenActiveAndStockGreaterThanZero_ShouldReturnTrue() {
        Product product = new Product();
        product.setActive(true);
        product.setStockQuantity(10);

        assertTrue(product.isAvailable());
    }

    @Test
    void testIsAvailable_WhenNotActive_ShouldReturnFalse() {
        Product product = new Product();
        product.setActive(false);
        product.setStockQuantity(10);

        assertFalse(product.isAvailable());
    }

    @Test
    void testIsAvailable_WhenStockZero_ShouldReturnFalse() {
        Product product = new Product();
        product.setActive(true);
        product.setStockQuantity(0);

        assertFalse(product.isAvailable());
    }

    @Test
    void testHasValidPrice_WhenPricePositive_ShouldReturnTrue() {
        Product product = new Product();
        product.setPrice(BigDecimal.valueOf(100.0));

        assertTrue(product.hasValidPrice());
    }

    @Test
    void testHasValidPrice_WhenPriceZero_ShouldReturnFalse() {
        Product product = new Product();
        product.setPrice(BigDecimal.ZERO);

        assertFalse(product.hasValidPrice());
    }

    @Test
    void testHasValidPrice_WhenPriceNull_ShouldReturnFalse() {
        Product product = new Product();
        product.setPrice(null);

        assertFalse(product.hasValidPrice());
    }
}
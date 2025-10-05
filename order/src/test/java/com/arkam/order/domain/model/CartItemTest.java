package com.arkam.order.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CartItemTest {

    @Test
    void testGetTotalPrice() {
        CartItem cartItem = new CartItem();
        cartItem.setPrice(BigDecimal.valueOf(100.0));
        cartItem.setQuantity(2);

        assertEquals(BigDecimal.valueOf(200.0), cartItem.getTotalPrice());
    }

    @Test
    void testIsValid_WhenValid_ShouldReturnTrue() {
        CartItem cartItem = new CartItem();
        cartItem.setUserId("user1");
        cartItem.setProductId("prod1");
        cartItem.setQuantity(1);
        cartItem.setPrice(BigDecimal.valueOf(50.0));

        assertTrue(cartItem.isValid());
    }

    @Test
    void testIsValid_WhenInvalid_ShouldReturnFalse() {
        CartItem cartItem = new CartItem();
        cartItem.setQuantity(0);

        assertFalse(cartItem.isValid());
    }
}
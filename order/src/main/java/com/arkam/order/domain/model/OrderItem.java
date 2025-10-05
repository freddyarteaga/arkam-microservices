package com.arkam.order.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    private Long id;
    private String productId;
    private Integer quantity;
    private BigDecimal price;

    // Domain business logic
    public BigDecimal getTotalPrice() {
        return price != null && quantity != null ? price.multiply(BigDecimal.valueOf(quantity)) : BigDecimal.ZERO;
    }

    public boolean isValid() {
        return productId != null && quantity != null && quantity > 0 && price != null && price.compareTo(BigDecimal.ZERO) > 0;
    }
}
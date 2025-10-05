package com.arkam.order.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CartItem {
    private Long id;
    private String userId;
    private String productId;
    private Integer quantity;
    private BigDecimal price;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Domain business logic
    public BigDecimal getTotalPrice() {
        return price != null && quantity != null ? price.multiply(BigDecimal.valueOf(quantity)) : BigDecimal.ZERO;
    }

    public boolean isValid() {
        return userId != null && productId != null && quantity != null && quantity > 0 && price != null && price.compareTo(BigDecimal.ZERO) > 0;
    }
}
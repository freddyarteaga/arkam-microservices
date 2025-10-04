package com.arkam.order.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderItem {
    private Long id;
    private String productId;
    private Integer quantity;
    private BigDecimal price;
    private Order order;
}

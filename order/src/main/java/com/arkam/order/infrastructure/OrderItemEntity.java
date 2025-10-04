package com.arkam.order.infrastructure;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderItemEntity {
    @Id
    private String id;
    private String productId;
    private Integer quantity;
    private BigDecimal price;
}
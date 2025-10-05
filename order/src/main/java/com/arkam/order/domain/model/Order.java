package com.arkam.order.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class Order {
    private Long id;
    private String userId;
    private BigDecimal totalAmount;
    private OrderStatus status = OrderStatus.PENDING;
    private List<OrderItem> items = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Domain business logic
    public void calculateTotalAmount() {
        this.totalAmount = items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean canBeProcessed() {
        return status == OrderStatus.PENDING && !items.isEmpty() && totalAmount != null && totalAmount.compareTo(BigDecimal.ZERO) > 0;
    }
}
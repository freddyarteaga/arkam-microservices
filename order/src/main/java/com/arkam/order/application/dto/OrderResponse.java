package com.arkam.order.application.dto;

import com.arkam.order.domain.model.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderResponse {
    private Long id;
    private String userId;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private List<OrderItemDTO> items;
}
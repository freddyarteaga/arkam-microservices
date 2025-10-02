package com.arkam.order.domain.dto;

import com.arkam.order.domain.model.CartItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {
    private List<CartItem> cartItems;
    private Integer totalQuantity;
    private BigDecimal totalPrice;
}

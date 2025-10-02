package com.arkam.product.application.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductRequestDto {
    private String name;
    private String description;

    @DecimalMin(value = "0.0", message = "El precio debe ser mayor a 0")
    private BigDecimal price;

    @Min(value = 0, message = "La cantidad en stock debe ser mayor o igual a 0")
    private Integer stockQuantity;

    private String category;
    private String imageUrl;
    private Boolean active;
}

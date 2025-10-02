package com.arkam.product.application.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductRequestDto {
    @NotBlank(message = "El nombre es requerido")
    private String name;

    @NotBlank(message = "La descripción es requerida")
    private String description;

    @NotNull(message = "El precio es requerido")
    @DecimalMin(value = "0.0", message = "El precio debe ser mayor a 0")
    private BigDecimal price;

    @NotNull(message = "La cantidad en stock es requerida")
    @Min(value = 0, message = "La cantidad en stock debe ser mayor o igual a 0")
    private Integer stockQuantity;

    @NotBlank(message = "La categoría es requerida")
    private String category;

    private String imageUrl;
}

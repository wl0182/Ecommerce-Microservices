package com.wassimlagnaoui.ecommerce.product_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductDTO {
    private String name;
    private String description;
    private double price;
    private String sku;
    private Long categoryId;
    private Integer stockQuantity;
}



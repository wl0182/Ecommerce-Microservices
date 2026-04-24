package com.wassimlagnaoui.ecommerce.product_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetails {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String categoryName;
    private String sku;
    private Integer stockQuantity;
}

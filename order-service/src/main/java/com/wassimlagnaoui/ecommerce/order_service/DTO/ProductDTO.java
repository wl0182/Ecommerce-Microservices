package com.wassimlagnaoui.ecommerce.order_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Comparator;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO  {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String categoryName;
    private String sku;

}


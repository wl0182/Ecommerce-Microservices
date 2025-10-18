package com.wassimlagnaoui.ecommerce.order_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Comparator;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO  {
    private Long id;
    private String name;
    private String description;
    private double price;
    private String categoryName;
    private String sku;

}


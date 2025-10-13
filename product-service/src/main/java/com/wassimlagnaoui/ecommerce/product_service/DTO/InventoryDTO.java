package com.wassimlagnaoui.ecommerce.product_service.DTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryDTO {
    private Long productId;
    private String productName;
    private String sku;
    private Integer stockInventory;
}

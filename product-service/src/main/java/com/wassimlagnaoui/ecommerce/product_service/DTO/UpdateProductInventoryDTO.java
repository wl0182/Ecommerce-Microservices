package com.wassimlagnaoui.ecommerce.product_service.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductInventoryDTO {
    private Long productId;
    private Integer quantity;
}

package com.wassimlagnaoui.ecommerce.order_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDTO {

    @NonNull
    private long productId;
    @NonNull
    private int quantity;

}

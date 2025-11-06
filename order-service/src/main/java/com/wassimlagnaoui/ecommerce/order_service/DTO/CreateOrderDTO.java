package com.wassimlagnaoui.ecommerce.order_service.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderDTO {

    @NonNull
    private Long userId;
    private List<OrderItemRequest> items;
    private String paymentMethod;
    private Long addressId;



} // { userId, items:[{ productId, quantity }], paymentMethod, addressId }

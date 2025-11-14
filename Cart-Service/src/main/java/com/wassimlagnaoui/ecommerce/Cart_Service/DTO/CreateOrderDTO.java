package com.wassimlagnaoui.ecommerce.Cart_Service.DTO;


import jakarta.persistence.Enumerated;
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

    // adding Validation for paymentMethod and addressId can be done here
    private String paymentMethod;
    private Long addressId;



} // { userId, items:[{ productId, quantity }], paymentMethod, addressId }

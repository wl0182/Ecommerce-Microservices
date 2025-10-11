package com.wassimlagnaoui.ecommerce.order_service.DTO;


import com.wassimlagnaoui.ecommerce.order_service.Entities.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderDTO {
    private long userId;
    private List<OrderItemDTO> items;
    private double totalAmount;
}

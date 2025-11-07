package com.wassimlagnaoui.common_events.Events.OrderService;


import com.wassimlagnaoui.common_events.Events.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreateEvent extends BaseEvent {
    private String orderId;
    private String userId;
    private double totalAmount;
    private String paymentMethod;
    private List<Item> items;
    private String createdAt;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Item { // Nested class to represent order items
        private String productId;
        private int quantity;
        private double price;
    }

} // { orderId, userId, totalAmount, paymentMethod, items:[{ productId, quantity, price }], createdAt }

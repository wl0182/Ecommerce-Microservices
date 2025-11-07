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
public class OrderCancelledEvent extends BaseEvent {
    private String orderId;
    private String userId;
    private List<Item> items;
    private String reason;
    private String cancelledAt;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Item {
        private String productId;
        private int quantity;
    }



} // { orderId, userId, items:[{ productId, quantity }], reason, cancelledAt }

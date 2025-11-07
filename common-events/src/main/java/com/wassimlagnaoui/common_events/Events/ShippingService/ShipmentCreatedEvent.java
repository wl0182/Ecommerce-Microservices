package com.wassimlagnaoui.common_events.Events.ShippingService;

import com.wassimlagnaoui.common_events.Events.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShipmentCreatedEvent extends BaseEvent {
    private Long shipmentId;
    private Long orderId;
    private Long userId;
    private String trackingNumber;
    private String status;
    private Instant createdAt;

}// { shipmentId, orderId, userId, trackingNumber, status, createdAt }

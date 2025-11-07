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
public class ShipmentUpdatedEvent extends BaseEvent {
    private Long shipmentId;
    private Long orderId;
    private String trackingNumber;
    private String status;
    private Instant updatedAt;

} // { shipmentId, orderId, trackingNumber, status, updatedAt }

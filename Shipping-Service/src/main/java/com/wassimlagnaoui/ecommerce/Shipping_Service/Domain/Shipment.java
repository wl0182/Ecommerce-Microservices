package com.wassimlagnaoui.ecommerce.Shipping_Service.Domain;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "shipments")
public class Shipment {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // Shipment → { id, orderId, carrier, trackingNumber, status[CREATED/IN_TRANSIT/DELIVERED/FAILED], estimatedDelivery, actualDelivery, createdAt }
    private Long orderId;
    private String carrier;
    private String trackingNumber;

    @Enumerated(EnumType.STRING)
    private ShipmentStatus status;

    private String estimatedDelivery;
    private String actualDelivery;
    private String createdAt;

} // JSON : { id, orderId, carrier, trackingNumber, status, estimatedDelivery, actualDelivery, createdAt }


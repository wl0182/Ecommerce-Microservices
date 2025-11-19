package com.wassimlagnaoui.ecommerce.Shipping_Service.Service;

import com.wassimlagnaoui.common_events.Events.ShippingService.ShipmentUpdatedEvent;
import com.wassimlagnaoui.common_events.KafkaTopics;
import com.wassimlagnaoui.ecommerce.Shipping_Service.DTO.ShipmentDTO;
import com.wassimlagnaoui.ecommerce.Shipping_Service.DTO.UpdateStatusRequest;
import com.wassimlagnaoui.ecommerce.Shipping_Service.DTO.UpdateStatusResponse;
import com.wassimlagnaoui.ecommerce.Shipping_Service.Domain.Shipment;
import com.wassimlagnaoui.ecommerce.Shipping_Service.Domain.ShipmentStatus;
import com.wassimlagnaoui.ecommerce.Shipping_Service.Exception.ShipmentNotFoundException;
import com.wassimlagnaoui.ecommerce.Shipping_Service.Exception.ShipmentStatusInvalid;
import com.wassimlagnaoui.ecommerce.Shipping_Service.Repository.ShipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;
import java.time.LocalDateTime;

@Service
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;

    @Autowired
    private KafkaPublisher kafkaPublisher;

    public ShipmentService(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    public ShipmentDTO getShipmentById(Long id) {
       Shipment shipment =shipmentRepository.findById(id).orElseThrow(() -> new ShipmentNotFoundException("Shipment not found with id: " + id));

         return ShipmentDTO.builder()
                .orderId(shipment.getOrderId())
                .carrier(shipment.getCarrier())
                .trackingNumber(shipment.getTrackingNumber())
                .status(shipment.getStatus().name())
                .estimatedDelivery(shipment.getEstimatedDelivery().toString())
                .actualDelivery(shipment.getActualDelivery() != null ? shipment.getActualDelivery().toString() : null)
                .createdAt(shipment.getCreatedAt().toString())
                .build();

    }


    // get Shipment by orderId
    public ShipmentDTO getShipmentByOrderId(Long orderId) {
        Shipment shipment = shipmentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ShipmentNotFoundException("Shipment not found with orderId: " + orderId));

        return ShipmentDTO.builder()
                .orderId(shipment.getOrderId())
                .carrier(shipment.getCarrier())
                .trackingNumber(shipment.getTrackingNumber())
                .status(shipment.getStatus().name())
                .estimatedDelivery(shipment.getEstimatedDelivery().toString())
                .actualDelivery(shipment.getActualDelivery() != null ? shipment.getActualDelivery().toString() : null)
                .createdAt(shipment.getCreatedAt().toString())
                .build();
    }


    @Transactional(rollbackForClassName = "ShipmentStatusInvalid")
    public UpdateStatusResponse updateShipmentStatus(UpdateStatusRequest request, Long id) {
       Shipment shipment = shipmentRepository.findById(id)
               .orElseThrow(() -> new ShipmentNotFoundException("Shipment not found with id: " + id));


       try {
           shipment.setStatus(ShipmentStatus.valueOf(request.getStatus()));
       } catch (IllegalArgumentException e) {
              throw new ShipmentStatusInvalid("Invalid status value: " + request.getStatus());
       }



         shipmentRepository.save(shipment);

       // publish Kafka event shipment.updated
        ShipmentUpdatedEvent event = ShipmentUpdatedEvent.builder()
                .shipmentId(shipment.getId())
                .orderId(shipment.getOrderId())
                .trackingNumber(shipment.getTrackingNumber())
                .status(request.getStatus())
                .updatedAt(Instant.now())
                .build();


        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                kafkaPublisher.publish(KafkaTopics.SHIPMENT_UPDATED, event);
            }
        });



        return UpdateStatusResponse.builder()
                .updatedAt(LocalDateTime.now().toString())
                .id(shipment.getId())
                .orderId(shipment.getOrderId())
                .trackingNumber(shipment.getTrackingNumber())
                .status(shipment.getStatus().name())
                .build();

    }









}

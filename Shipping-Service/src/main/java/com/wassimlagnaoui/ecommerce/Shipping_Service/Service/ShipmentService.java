package com.wassimlagnaoui.ecommerce.Shipping_Service.Service;

import com.wassimlagnaoui.common_events.Events.PaymentService.OrderPaidEvent;
import com.wassimlagnaoui.common_events.Events.ShippingService.ShipmentCreatedEvent;
import com.wassimlagnaoui.common_events.Events.ShippingService.ShipmentUpdatedEvent;
import com.wassimlagnaoui.common_events.KafkaTopics;
import com.wassimlagnaoui.ecommerce.Shipping_Service.DTO.ShipRequest;
import com.wassimlagnaoui.ecommerce.Shipping_Service.DTO.ShipmentDTO;
import com.wassimlagnaoui.ecommerce.Shipping_Service.DTO.UpdateStatusRequest;
import com.wassimlagnaoui.ecommerce.Shipping_Service.DTO.UpdateStatusResponse;
import com.wassimlagnaoui.ecommerce.Shipping_Service.Domain.Shipment;
import com.wassimlagnaoui.ecommerce.Shipping_Service.Domain.ShipmentStatus;
import com.wassimlagnaoui.ecommerce.Shipping_Service.Exception.InvalidAddressValidation;
import com.wassimlagnaoui.ecommerce.Shipping_Service.Exception.ShipmentNotFoundException;
import com.wassimlagnaoui.ecommerce.Shipping_Service.Exception.ShipmentStatusInvalid;
import com.wassimlagnaoui.ecommerce.Shipping_Service.Repository.ShipmentRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDateTime;

@Slf4j
@Service
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;

    @Autowired
    private KafkaPublisher kafkaPublisher;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${services.user-service.url}")
    private String userServiceUrl;

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


    // create shipment
    @Transactional
    public ShipmentDTO createShipmentLabel(OrderPaidEvent orderPaidEvent) {

        // we create a shipment for the order
        Shipment shipment = new Shipment();
        shipment.setOrderId(orderPaidEvent.getOrderId());
        shipment.setCarrier("DHL"); // for example
        shipment.setTrackingNumber("TRACK" + orderPaidEvent.getOrderId() + System.currentTimeMillis());
        shipment.setStatus(ShipmentStatus.PENDING);
        shipment.setEstimatedDelivery(LocalDateTime.now().plusDays(5).toString());
        shipment.setCreatedAt(LocalDateTime.now().toString());

        Shipment savedShipment = shipmentRepository.save(shipment);

        return ShipmentDTO.builder()
                .ShipmentId(savedShipment.getId())
                .orderId(shipment.getOrderId())
                .carrier(shipment.getCarrier())
                .trackingNumber(shipment.getTrackingNumber())
                .status(shipment.getStatus().name())
                .estimatedDelivery(shipment.getEstimatedDelivery().toString())
                .actualDelivery(shipment.getActualDelivery() != null ? shipment.getActualDelivery().toString() : null)
                .createdAt(shipment.getCreatedAt().toString())
                .build();
    }



    @Transactional
    public ShipmentDTO shipOrder(Long orderId,ShipRequest shipRequest){
        // Validate User and Address via REST call to User-Service
        Boolean isValidAddress = validateAddress(shipRequest.getUserId(), shipRequest.getAddressId());
        if(!isValidAddress){
            throw new InvalidAddressValidation("Invalid Address  " + shipRequest.getUserId() + ", addressId: " + shipRequest.getAddressId());

        }
        // update shipment status to SHIPPED
        Shipment shipment = shipmentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ShipmentNotFoundException("Shipment not found with orderId: " + shipRequest.getOrderId()));

        shipment.setStatus(ShipmentStatus.SHIPPED);
        shipmentRepository.save(shipment);


        ShipmentCreatedEvent shipmentCreatedEvent = ShipmentCreatedEvent.builder()
                .shipmentId(shipment.getId())
                .orderId(shipment.getOrderId())
                .trackingNumber(shipment.getTrackingNumber())
                .status(shipment.getStatus().name())
                .createdAt(Instant.now())
                .build();

        // publish event after transaction commit
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                kafkaPublisher.publish(KafkaTopics.SHIPMENT_CREATED, shipmentCreatedEvent);
            }
        });



        // return ShipmentDTO
        return ShipmentDTO.builder()
                .ShipmentId(shipment.getId())
                .orderId(shipment.getOrderId())
                .carrier(shipment.getCarrier())
                .trackingNumber(shipment.getTrackingNumber())
                .status(shipment.getStatus().name())
                .estimatedDelivery(shipment.getEstimatedDelivery().toString())
                .actualDelivery(shipment.getActualDelivery() != null ? shipment.getActualDelivery().toString() : null)
                .createdAt(shipment.getCreatedAt().toString())
                .build();


    }


    @CircuitBreaker(name = "userServiceCircuitBreaker", fallbackMethod = "validateAddressFallback")
    public Boolean validateAddress(Long userId, Long addressId){
        // REST call to User-Service to validate address
        String url = userServiceUrl + "/users/" + userId + "/addresses/" + addressId + "/validate";
        Boolean isValid = restTemplate.getForObject(url, Boolean.class);
        if(isValid != null){
            return isValid;
        }
        return false;
    }

    // Fallback method for Circuit Breaker
    public Boolean validateAddressFallback(Long userId, Long addressId, Throwable throwable){
        // In case of failure, we assume the address is invalid
        log.info("Fallback: Unable to validate address for userId: " + userId + ", addressId: " + addressId + ". Error: " + throwable.getMessage());
        return false;
    }











}

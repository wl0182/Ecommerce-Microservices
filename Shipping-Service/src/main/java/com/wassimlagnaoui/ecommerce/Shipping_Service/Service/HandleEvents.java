package com.wassimlagnaoui.ecommerce.Shipping_Service.Service;

import com.wassimlagnaoui.common_events.Events.PaymentService.OrderPaidEvent;
import com.wassimlagnaoui.common_events.Events.ShippingService.ShipmentCreatedEvent;
import com.wassimlagnaoui.common_events.KafkaGroupIds;
import com.wassimlagnaoui.common_events.KafkaTopics;
import com.wassimlagnaoui.ecommerce.Shipping_Service.DTO.ShipmentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;

@Service
public class HandleEvents {


   private final ShipmentService shipmentService;

   @Autowired
   private KafkaPublisher kafkaPublisher;

    public HandleEvents(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @KafkaListener(topics = KafkaTopics.ORDER_PAID, groupId = KafkaGroupIds.SHIPPING_SERVICE_GROUP)
    public void handleOrderPaidEvent(OrderPaidEvent orderPaidEvent){
        ShipmentDTO shipmentDTO = shipmentService.createShipmentLabel(orderPaidEvent);
    }



}

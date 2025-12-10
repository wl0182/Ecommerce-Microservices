package com.wassimlagnaoui.ecommerce.order_service.Service;


import com.wassimlagnaoui.common_events.Events.PaymentService.OrderPaidEvent;
import com.wassimlagnaoui.common_events.Events.PaymentService.PaymentFailed;
import com.wassimlagnaoui.common_events.Events.PaymentService.PaymentProcessed;
import com.wassimlagnaoui.common_events.Events.PaymentService.PaymentRefunded;
import com.wassimlagnaoui.common_events.Events.ShippingService.ShipmentCreatedEvent;
import com.wassimlagnaoui.common_events.Events.ShippingService.ShipmentUpdatedEvent;
import com.wassimlagnaoui.common_events.KafkaGroupIds;
import com.wassimlagnaoui.common_events.KafkaTopics;
import com.wassimlagnaoui.ecommerce.order_service.Entities.Order;
import com.wassimlagnaoui.ecommerce.order_service.Entities.OrderStatus;
import com.wassimlagnaoui.ecommerce.order_service.Exception.OrderNotFound;
import com.wassimlagnaoui.ecommerce.order_service.Repository.OrderItemRepository;
import com.wassimlagnaoui.ecommerce.order_service.Repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;

@Slf4j
@Service
public class HandleEvents {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Autowired
    private KafkaPublisher kafkaPublisher;

    public HandleEvents(OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    /*
    #### Service Methods Triggered by Kafka Events
- On payment-processed (C): Update order status to PAID
- On payment-refunded (C): Update order status to REFUNDED
- On payment-failed (C): Update order status to PAYMENT_FAILED
     */


    @KafkaListener(topics = KafkaTopics.PAYMENT_PROCESSED, groupId = KafkaGroupIds.ORDER_SERVICE_GROUP)
    @Transactional
    public void handlePaymentProcessed(PaymentProcessed paymentProcessed) {
        Order order = orderRepository.findById(paymentProcessed.getOrderId()).orElseThrow(()-> new OrderNotFound("Order not found with id: " + paymentProcessed.getOrderId()+" for payment processed event"));
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        OrderPaidEvent orderPaidEvent = new OrderPaidEvent();
        orderPaidEvent.setOrderId(order.getId());
        orderPaidEvent.setUserId(order.getUserId());
        orderPaidEvent.setAmount(BigDecimal.valueOf(paymentProcessed.getAmount()));
        orderPaidEvent.setPaymentMethod(paymentProcessed.getPaymentMethod());
        orderPaidEvent.setPaymentStatus(paymentProcessed.getStatus());
        orderPaidEvent.setPaidAt(paymentProcessed.getCreatedAt());


        // { orderId, userId, amount, paymentMethod, paymentStatus, paidAt }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                kafkaPublisher.publish(KafkaTopics.ORDER_PAID,orderPaidEvent);
            }
        });

        log.info("Order with id: {} updated to PAID status after payment processed event", order.getId());
    }


    @KafkaListener(topics = KafkaTopics.PAYMENT_REFUNDED, groupId = KafkaGroupIds.ORDER_SERVICE_GROUP)
    public void handlePaymentRefund(PaymentRefunded paymentRefunded){
        Order order = orderRepository.findById(paymentRefunded.getOrderId()).orElseThrow(()-> new OrderNotFound("Order not found with id: " + paymentRefunded.getOrderId()+" for payment refunded event"));
        order.setStatus(OrderStatus.REFUNDED); // assuming refunded orders are marked as CANCELED
        orderRepository.save(order);

        log.info("Order with id: {} updated to Canceled status after payment refunded event", order.getId());
    }

    // Handle payment failed event
    @KafkaListener(topics = KafkaTopics.PAYMENT_FAILED, groupId = KafkaGroupIds.ORDER_SERVICE_GROUP)
    public void handlePaymentFailed(PaymentFailed paymentFailed) {
        Order order = orderRepository.findById(paymentFailed.getOrderId()).orElseThrow(()-> new OrderNotFound("Order not found with id: " + paymentFailed.getOrderId()+" for payment failed event"));
        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);
        log.info("Order with id: {} updated to CANCELED status after payment failed event", order.getId());

    }

    @KafkaListener(topics = KafkaTopics.SHIPMENT_CREATED,groupId = KafkaGroupIds.ORDER_SERVICE_GROUP )
    public void handleShipmentCreated(ShipmentCreatedEvent shipmentCreatedEvent){
        Order order = orderRepository.findById(shipmentCreatedEvent.getOrderId()).orElseThrow(()-> new OrderNotFound("Order not found with id: " + shipmentCreatedEvent.getOrderId()+" for shipment created event"));
        order.setStatus(OrderStatus.SHIPPED);
        orderRepository.save(order);
        log.info("Order with id: {} updated to SHIPPED status after shipment created event", order.getId());
    }


    // Handle shipment updated event
    @KafkaListener(topics = KafkaTopics.SHIPMENT_UPDATED,groupId = KafkaGroupIds.ORDER_SERVICE_GROUP)
    public void handShipmentUpdated(ShipmentUpdatedEvent shipmentUpdatedEvent){
        Order order = orderRepository.findById(shipmentUpdatedEvent.getOrderId()).orElseThrow(()-> new OrderNotFound("Order not found with id: " + shipmentUpdatedEvent.getOrderId()+" for shipment updated event"));
        order.setStatus(OrderStatus.valueOf(shipmentUpdatedEvent.getStatus()));
        orderRepository.save(order);
        log.info("Order with id: {} updated to {} status after shipment updated event", order.getId(),shipmentUpdatedEvent.getStatus());
    }











}

package com.wassimlagnaoui.ecommerce.Notification_Service.Service;

import com.wassimlagnaoui.common_events.DummyEvent;
import com.wassimlagnaoui.common_events.Events.OrderService.OrderCancelledEvent;
import com.wassimlagnaoui.common_events.Events.OrderService.OrderCreateEvent;
import com.wassimlagnaoui.common_events.Events.PaymentService.PaymentFailed;
import com.wassimlagnaoui.common_events.Events.PaymentService.PaymentProcessed;
import com.wassimlagnaoui.common_events.Events.ShippingService.ShipmentCreatedEvent;
import com.wassimlagnaoui.common_events.Events.ShippingService.ShipmentUpdatedEvent;
import com.wassimlagnaoui.common_events.Events.UserService.UserRegisteredEvent;
import com.wassimlagnaoui.common_events.KafkaGroupIds;
import com.wassimlagnaoui.common_events.KafkaTopics;
import jakarta.mail.MessagingException;
import org.apache.kafka.common.protocol.types.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventListener {

    @Autowired
    private TemplateService templateService;


    private static final Logger log = LoggerFactory.getLogger(NotificationEventListener.class);

    @Autowired
    private EmailService emailService;

    @KafkaListener(topics = KafkaTopics.TOPIC_PRODUCT_TEST,groupId = KafkaGroupIds.NOTIFICATION_SERVICE_GROUP)
    public void  testListener(DummyEvent event) {
        System.out.println("Received Dummy Event in Notification Service: " + event.getMessage()+" at "+event.getTimestamp());
        log.info(event.getMessage());
    }


    @KafkaListener(topics = KafkaTopics.USER_REGISTERED,groupId = KafkaGroupIds.NOTIFICATION_SERVICE_GROUP)
    public void handleUserRegisteredEvent(UserRegisteredEvent event)  {
        // Handle the event (e.g., send a welcome notification)
        String subject = "Welcome to Our E-commerce Platform!";
        String body = templateService.renderUserRegisteredTemplate(event.getName());
        emailService.sendEmail(event.getEmail(),subject,body);
        System.out.println("Processed UserRegisteredEvent for user: " + event.getEmail());
        log.info("Processed UserRegisteredEvent for user: " + event.getEmail());
        log.info("Email Sent to User");
    }

    // handle Order CreatedEvent to send order confirmation email
    @KafkaListener(topics = KafkaTopics.ORDER_CREATED,groupId = KafkaGroupIds.NOTIFICATION_SERVICE_GROUP)
    public void handleOrderCreatedEvent(OrderCreateEvent event) {
        // Handle the event (e.g., send an order confirmation notification)
        String subject = "Order Confirmation - Order #" + event.getOrderId();
        String body = templateService.renderOrderCreatedTemplate(event);
        emailService.sendEmail("lagnaouiw@gmail.com",subject,body);
        System.out.println("Processed OrderCreateEvent for order: " + event.getOrderId());
        log.info("Processed OrderCreateEvent for order: " + event.getOrderId());
        log.info("Email Sent to User for Order Confirmation");
    }

    // handle Payment Processed Event to send payment confirmation email
    @KafkaListener(topics = KafkaTopics.PAYMENT_PROCESSED,groupId = KafkaGroupIds.NOTIFICATION_SERVICE_GROUP)
    public void handlePaymentProcessedEvent(PaymentProcessed paymentProcessed) {
        // Implementation goes here
        String subject = "Payment Confirmation - Order #" + paymentProcessed.getOrderId();
        String body = templateService.renderPaymentProcessedTemplate(paymentProcessed);
        emailService.sendEmail("lagnaouiw@gmail.com",subject,body);
        System.out.println("Processed PaymentProcessed Event for order: " + paymentProcessed.getOrderId());
        log.info("Processed PaymentProcessed Event for order: " + paymentProcessed.getOrderId());
        log.info("Email Sent to User for Payment Confirmation");
    }

    // handle payment failed event to send payment failure email
    @KafkaListener(topics = KafkaTopics.PAYMENT_FAILED,groupId = KafkaGroupIds.NOTIFICATION_SERVICE_GROUP)
    public void handlePaymentFailedEvent(PaymentFailed paymentFailed) {
        String subject = "Payment Failed - Order #" + paymentFailed.getOrderId();
        String body = templateService.renderPaymentFailedTemplate(paymentFailed);
        emailService.sendEmail("lagnaouiw@gmail.com",subject,body);
        System.out.println("Processed PaymentFailed Event for order: " + paymentFailed.getOrderId());
        log.info("Processed PaymentFailed Event for order: " + paymentFailed.getOrderId());
        log.info("Email Sent to User for Payment Failure");
    }

//    - On shipment-created (C): Send shipment created email/SMS
    @KafkaListener(topics = KafkaTopics.SHIPMENT_CREATED, groupId =KafkaGroupIds.NOTIFICATION_SERVICE_GROUP )
    public void handleShipmentCreatedEvent(ShipmentCreatedEvent shipmentCreatedEvent) {
        // Implementation goes here
        String subject = "Shipment Created - Order #" + shipmentCreatedEvent.getOrderId();
        String body = templateService.renderShipmentCreatedTemplate(shipmentCreatedEvent);
        emailService.sendEmail("lagnaouiw@gmail.com",subject,body);
        System.out.println("Processed ShipmentCreatedEvent for order: " + shipmentCreatedEvent.getOrderId());
        log.info("Processed ShipmentCreatedEvent for order: " + shipmentCreatedEvent.getOrderId());
        log.info("Email Sent to User for Shipment Created");
    }


//- On shipment-updated (C): Send shipment status update email/SMS
    @KafkaListener(topics = KafkaTopics.SHIPMENT_UPDATED, groupId =KafkaGroupIds.NOTIFICATION_SERVICE_GROUP )
    public void handleShipmentUpdatedEvent(ShipmentUpdatedEvent shipmentUpdatedEvent) {
        // Implementation goes here
        String subject = "Shipment Update - Order #" + shipmentUpdatedEvent.getOrderId();
        String body = templateService.renderShipmentUpdatedTemplate(shipmentUpdatedEvent);
        emailService.sendEmail("lagnaouiw@gmail.com",subject,body);
        System.out.println("Processed ShipmentUpdatedEvent for order: " + shipmentUpdatedEvent.getOrderId());
        log.info("Processed ShipmentUpdatedEvent for order: " + shipmentUpdatedEvent.getOrderId());
        log.info("Email Sent to User for Shipment Update");
    }

//- On order-cancelled (C): Send order cancellation email/SMS
    @KafkaListener(topics = KafkaTopics.ORDER_CANCELLED, groupId =KafkaGroupIds.NOTIFICATION_SERVICE_GROUP )
    public void handleOrderCancelledEvent(OrderCancelledEvent orderCancelledEvent) {
        // Implementation goes here
        String subject = "Order Cancelled - Order #" + orderCancelledEvent.getOrderId();
        String body = templateService.renderOrderCancelledTemplate(orderCancelledEvent);
        emailService.sendEmail("lagnaouiw@gmail.com", subject, body);
        log.info("Processed OrderCancelledEvent for order: " + orderCancelledEvent.getOrderId());
        log.info("Email Sent to User for Order Cancellation");
    }














}

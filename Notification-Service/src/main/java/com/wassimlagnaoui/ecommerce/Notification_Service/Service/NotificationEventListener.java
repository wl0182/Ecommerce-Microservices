package com.wassimlagnaoui.ecommerce.Notification_Service.Service;

import com.wassimlagnaoui.common_events.DummyEvent;
import com.wassimlagnaoui.common_events.Events.OrderService.OrderCreateEvent;
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









}

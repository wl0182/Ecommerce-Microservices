package com.wassimlagnaoui.ecommerce.Notification_Service.Service;

import com.wassimlagnaoui.common_events.DummyEvent;
import com.wassimlagnaoui.common_events.Events.UserService.UserRegisteredEvent;
import com.wassimlagnaoui.common_events.KafkaGroupIds;
import com.wassimlagnaoui.common_events.KafkaTopics;
import org.apache.kafka.common.protocol.types.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventListener {



    private static final Logger log = LoggerFactory.getLogger(NotificationEventListener.class);

    @Autowired
    private EmailService emailService;

    @KafkaListener(topics = KafkaTopics.TOPIC_PRODUCT_TEST,groupId = KafkaGroupIds.NOTIFICATION_SERVICE_GROUP)
    public void  testListener(DummyEvent event) {
        System.out.println("Received Dummy Event in Notification Service: " + event.getMessage()+" at "+event.getTimestamp());
        log.info(event.getMessage());
    }


    @KafkaListener(topics = KafkaTopics.USER_REGISTERED,groupId = KafkaGroupIds.NOTIFICATION_SERVICE_GROUP)
    public void handleUserRegisteredEvent(UserRegisteredEvent event) {
        // Handle the event (e.g., send a welcome notification)
        String subject = "Welcome to Our E-commerce Platform!";
        String body = "Dear "+ event.getName() +" ,\n\nThank you for registering with us. We're excited to have you on board!\n\nBest regards,\nE-commerce Team";
        emailService.sendEmail(event.getEmail(),subject,body);
        System.out.println("Processed UserRegisteredEvent for user: " + event.getEmail());
        log.info("Processed UserRegisteredEvent for user: " + event.getEmail());
        log.info("Email Sent to User");
    }





}

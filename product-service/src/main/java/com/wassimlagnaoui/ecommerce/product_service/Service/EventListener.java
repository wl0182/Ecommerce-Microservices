package com.wassimlagnaoui.ecommerce.product_service.Service;


import org.springframework.kafka.annotation.KafkaListeners;
import org.springframework.stereotype.Component;
import org.springframework.kafka.annotation.KafkaListener;


@Component
public class EventListener {

    // Event listener methods will be defined here
    @KafkaListener(topics = "product-test-topic", groupId = "Group1") // Listen to messages from the "product-topic" topic
    public void consumeMessage(String message) {
        System.out.println("Received message: " + message);
    }



}

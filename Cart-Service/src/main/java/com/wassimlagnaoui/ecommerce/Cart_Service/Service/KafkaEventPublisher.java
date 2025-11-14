package com.wassimlagnaoui.ecommerce.Cart_Service.Service;

import com.wassimlagnaoui.common_events.Events.CartService.CartClearedEvent;
import com.wassimlagnaoui.common_events.KafkaTopics;
import com.wassimlagnaoui.ecommerce.Cart_Service.Exception.KafkaPublisherException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class KafkaEventPublisher {

    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public KafkaEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @CircuitBreaker(name = "kafkaCircuitBreaker", fallbackMethod = "publishFallback")
    @Retry(name ="cartServiceRetry", fallbackMethod = "publishFallback")
    public void publishCartClearedEvent(CartClearedEvent event) {
        try {
            kafkaTemplate.send(KafkaTopics.CART_CLEARED, event).get();
            log.info("Published CartClearedEvent to Kafka for userId: {}", event.getUserId());
        } catch (Exception e) {
            throw new KafkaPublisherException("Failed to publish CartClearedEvent to Kafka: " + e.getMessage());
        }
    }

    public void publishFallback(CartClearedEvent event, Throwable throwable) {
        // Log the failure or take alternative action
       log.info("Failed to publish CartClearedEvent for userId: {}. Error: {}", event.getUserId(), throwable.getMessage());
    }

    // the cart cleared event is published when the cart is successfully cleared, no consumers for now. orphan event.



}

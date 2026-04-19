package com.wassimlagnaoui.ecommerce.order_service.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wassimlagnaoui.common_events.Events.OrderService.OrderCreateEvent;
import com.wassimlagnaoui.common_events.KafkaTopics;
import com.wassimlagnaoui.ecommerce.order_service.Entities.EventStatus;
import com.wassimlagnaoui.ecommerce.order_service.Entities.OrderOutbox;
import com.wassimlagnaoui.ecommerce.order_service.Repository.OrderOutboxRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class OrderKafkaPublisher {

    private final KafkaTemplate<String,Object> kafkaTemplate;
    private final OrderOutboxRepository orderOutboxRepository;


    public OrderKafkaPublisher(KafkaTemplate<String, Object> kafkaTemplate, OrderOutboxRepository orderOutboxRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.orderOutboxRepository = orderOutboxRepository;
    }

    @Autowired
    private ObjectMapper objectMapper;


    public void publish(String topic, Object message){
            kafkaTemplate.send(topic,message).whenComplete((result, ex) -> {;
                if (ex != null) {
                    log.error("Failed to publish message to topic {}: {}", topic, ex.getMessage());
                } else {
                    log.info("Message published to topic {} partition {} with offset {}", topic, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
                }
            });
    }

    // Event registry
    Map<String,Class<?>> registry = new HashMap<>();

    {
        registry.put(KafkaTopics.ORDER_CREATED, OrderCreateEvent.class);
    }


    // publish unprocess events
    @Transactional
    @Scheduled(fixedDelay = 3000)
    public void publishUnprocessedEvents(){
        List<OrderOutbox> events = orderOutboxRepository.findUnprocessedEvents();

        for (OrderOutbox event : events){
            try {
                Class<?> eventClass = registry.get(event.getEventType());
                if (eventClass == null) {
                    throw new IllegalArgumentException("Unknown event type: " + event.getEventType());
                }
                Object eventObject = objectMapper.readValue(event.getPayload(), eventClass);
                kafkaTemplate.send(event.getEventType(), eventObject).get(2, TimeUnit.SECONDS); // Wait for the send to complete
                event.setStatus(EventStatus.PROCESSED);
                event.setProcessedAt(LocalDateTime.now());
                orderOutboxRepository.save(event);
                log.info("Successfully published event with id {} of type {}", event.getId(), event.getEventType());
            }catch (Exception e){
                log.error("Failed to publish event with id {}: {}", event.getId(), e.getMessage());
                event.setStatus(EventStatus.FAILED);
                event.setErrorMessage(e.getMessage());
                event.setRetryCount(event.getRetryCount() + 1);
                orderOutboxRepository.save(event);
            }
        }
    }




}

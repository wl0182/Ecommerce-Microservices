package com.wassimlagnaoui.ecommerce.product_service.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wassimlagnaoui.common_events.Events.ProductService.ProductUpdatedEvent;
import com.wassimlagnaoui.common_events.KafkaTopics;
import com.wassimlagnaoui.ecommerce.product_service.Domain.EventStatus;
import com.wassimlagnaoui.ecommerce.product_service.Domain.ProductOutbox;
import com.wassimlagnaoui.ecommerce.product_service.Repository.ProductOutboxRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.internals.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ProductKafkaPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ProductOutboxRepository productOutboxRepository;

    public ProductKafkaPublisher(KafkaTemplate<String, Object> kafkaTemplate, ProductOutboxRepository productOutboxRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.productOutboxRepository = productOutboxRepository;
    }

    @Autowired
    private ObjectMapper objectMapper;


    Map<String, Class<?>> eventTypeMapping = new HashMap<>();

    {
        eventTypeMapping.put(KafkaTopics.PRODUCT_UPDATED, ProductUpdatedEvent.class);
        // Add more mappings as needed
    }



    @Scheduled(fixedDelay = 3600000)
    public void publishOutboxEvents(){
        List<ProductOutbox> pendingEvents = productOutboxRepository.findUnprocessedEvents();
        for (ProductOutbox event : pendingEvents) {
            try {
                Class<?> eventClass = eventTypeMapping.get(event.getEventType());

                if (eventClass == null) {
                    throw new IllegalArgumentException("Unknown event type: " + event.getEventType());
                }

                Object eventObject = objectMapper.readValue(event.getPayload(), eventClass);
                kafkaTemplate.send(event.getEventType(), eventObject).get();

                log.info("Published event to Kafka: {}", event.getEventType());
                log.info("Event payload: {}", event.getPayload());
                event.setStatus(EventStatus.PROCESSED);
                event.setProcessedAt(LocalDateTime.now());

                productOutboxRepository.save(event);
            }catch (Exception e) {

                event.setStatus(EventStatus.FAILED);
                event.setErrorMessage(e.getMessage());

                productOutboxRepository.save(event);
            }
        }
    }



}

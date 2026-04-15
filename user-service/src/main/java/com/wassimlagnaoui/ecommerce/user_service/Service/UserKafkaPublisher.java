package com.wassimlagnaoui.ecommerce.user_service.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wassimlagnaoui.ecommerce.user_service.Model.EventStatus;
import com.wassimlagnaoui.ecommerce.user_service.Model.UserOutboxEvent;
import com.wassimlagnaoui.ecommerce.user_service.Repository.UserOutboxRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class UserKafkaPublisher {
    // inject KafkaTemplate
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final UserOutboxRepository userOutboxRepository;

    private final ObjectMapper objectMapper;

    public UserKafkaPublisher(KafkaTemplate<String, Object> kafkaTemplate, UserOutboxRepository userOutboxRepository, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.userOutboxRepository = userOutboxRepository;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelay = 1000) // Run every 5 seconds
    public void publishEvents() {
        log.info("Publishing UserRegisteredEvent to Kafka...");
        // Fetch pending events from the outbox
        List<UserOutboxEvent> unprocessedEvents = userOutboxRepository.findUnprocessedEvents();

        for (UserOutboxEvent event : unprocessedEvents) {
            try {
                kafkaTemplate.send(event.getEventType(), event.getPayload());
                // Update event status to PROCESSED
                event.setStatus(EventStatus.PROCESSED);
                event.setProcessedAt(LocalDateTime.now());
                userOutboxRepository.save(event);
                log.info("Published event with ID: {}", event.getId());
            } catch (Exception e) {
                log.error("Failed to publish event with ID: {}. Error: {}", event.getId(), e.getMessage());
                // Update event status to FAILED and increment retry count
                event.setStatus(EventStatus.FAILED);
                event.setRetryCount(event.getRetryCount() + 1);
                event.setErrorMessage(e.getMessage());
                userOutboxRepository.save(event);
            }
        }


    }
}

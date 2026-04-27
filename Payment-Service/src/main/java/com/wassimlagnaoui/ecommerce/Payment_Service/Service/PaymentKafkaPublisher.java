package com.wassimlagnaoui.ecommerce.Payment_Service.Service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.wassimlagnaoui.common_events.Events.PaymentService.PaymentFailed;
import com.wassimlagnaoui.common_events.Events.PaymentService.PaymentProcessed;
import com.wassimlagnaoui.common_events.Events.PaymentService.PaymentRefunded;
import com.wassimlagnaoui.common_events.KafkaTopics;
import com.wassimlagnaoui.ecommerce.Payment_Service.Domain.EventStatus;
import com.wassimlagnaoui.ecommerce.Payment_Service.Domain.PaymentOutbox;
import com.wassimlagnaoui.ecommerce.Payment_Service.Repository.PaymentOutboxRepository;
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
public class PaymentKafkaPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final PaymentOutboxRepository paymentOutboxRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public PaymentKafkaPublisher(KafkaTemplate<String, Object> kafkaTemplate, PaymentOutboxRepository paymentOutboxRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.paymentOutboxRepository = paymentOutboxRepository;
    }


    public void publish(String topic, Object message) {
        kafkaTemplate.send(topic, message).whenComplete((stringObjectSendResult, throwable) -> {
            if (throwable != null) {
                log.info("Failed to publish message to topic " + topic + ": " + throwable.getMessage());
            } else {
               log.info("Message published to topic " + topic + " with offset " + stringObjectSendResult.getRecordMetadata().offset());
            }
        });
    }

    private Map<String,Class<?>> eventClassRegistry = new HashMap<>();

    {
        eventClassRegistry.put(KafkaTopics.PAYMENT_FAILED, PaymentFailed.class);
        eventClassRegistry.put(KafkaTopics.PAYMENT_PROCESSED, PaymentProcessed.class);
        eventClassRegistry.put(KafkaTopics.PAYMENT_REFUNDED, PaymentRefunded.class);
    }




    @Scheduled(fixedDelay = 5000)
    @Transactional// Run every 5 seconds
    public void publishOutboxMessages() {
        List<PaymentOutbox> pendingEvents = paymentOutboxRepository.findByStatus(EventStatus.PENDING);
        for (PaymentOutbox event : pendingEvents) {
            try {
                String eventType = event.getEventType();
                Object eventObject = objectMapper.readValue(event.getPayload(),eventClassRegistry.get(eventType));
                kafkaTemplate.send(eventType,eventObject).get(2, TimeUnit.SECONDS);

                log.info("Published event to topic " + eventType + " with payload: " + event.getPayload());
                event.setProcessedAt(LocalDateTime.now());
                event.setStatus(EventStatus.PROCESSED);
                paymentOutboxRepository.save(event);
                log.info("Updated event status to PROCESSED for event id: " + event.getId());
            } catch (Exception ex) {
                log.info("Failed to publish event with id " + event.getId() + ": " + ex.getMessage());
                String errorMessage = ex.getMessage();
                event.setErrorMessage(errorMessage);
                if (event.getRetryCount()>5){
                    event.setStatus(EventStatus.FAILED);
                }
                event.setRetryCount(event.getRetryCount()+1);
                paymentOutboxRepository.save(event);
            }
        }
    }





}

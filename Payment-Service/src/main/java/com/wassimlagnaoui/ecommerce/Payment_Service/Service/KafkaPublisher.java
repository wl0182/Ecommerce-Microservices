package com.wassimlagnaoui.ecommerce.Payment_Service.Service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
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


}

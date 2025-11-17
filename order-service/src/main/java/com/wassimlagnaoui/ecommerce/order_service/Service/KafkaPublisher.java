package com.wassimlagnaoui.ecommerce.order_service.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class KafkaPublisher {

    private final KafkaTemplate<String,Object> kafkaTemplate;

    public KafkaPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(String topic, Object message){
            kafkaTemplate.send(topic,message).whenComplete((result, ex) -> {;
                if (ex != null) {
                    log.error("Failed to publish message to topic {}: {}", topic, ex.getMessage());
                } else {
                    log.info("Message published to topic {} partition {} with offset {}", topic, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
                }
            });
    }

}

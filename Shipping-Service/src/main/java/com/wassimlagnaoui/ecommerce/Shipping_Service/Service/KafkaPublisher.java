package com.wassimlagnaoui.ecommerce.Shipping_Service.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaPublisher {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;


    public void publish(String topic, Object message) {

        kafkaTemplate.send(topic, message).whenComplete((result, ex) -> {
            if (ex != null) {
                log.info("Failed to send message to topic " + topic + ": " + ex.getMessage());
            } else {
               log.info("Message sent to topic " + topic + " with offset " + result.getRecordMetadata().offset());
            }
        });
    }



}

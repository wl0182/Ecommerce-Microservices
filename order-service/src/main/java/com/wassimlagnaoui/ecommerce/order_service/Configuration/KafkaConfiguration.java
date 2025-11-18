package com.wassimlagnaoui.ecommerce.order_service.Configuration;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.wassimlagnaoui.common_events.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfiguration {
    // New Topic Bean
    @Bean
    public NewTopic orderCreatedTopic(){
        return new NewTopic(KafkaTopics.ORDER_CREATED,3,(short)1); // 3 partitions, 1 replica
    }


    @Bean
    public NewTopic orderCancelledTopic(){
        return new NewTopic(KafkaTopics.ORDER_CANCELLED,2,(short)1); // 2 partitions, 1 replica
    }

    // order paid topic
    @Bean
    public NewTopic orderPaidTopic(){
        return new NewTopic(KafkaTopics.ORDER_PAID,2,(short)1); // 2 partitions, 1 replica
    }





}

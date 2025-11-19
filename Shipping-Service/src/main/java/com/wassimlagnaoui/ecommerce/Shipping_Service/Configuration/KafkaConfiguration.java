package com.wassimlagnaoui.ecommerce.Shipping_Service.Configuration;

import com.wassimlagnaoui.common_events.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfiguration {

    @Bean
    public NewTopic shippingCreatedTopic() {
        return new NewTopic(KafkaTopics.SHIPMENT_CREATED, 2, (short) 1); // 2 partitions, 1 replica
    }

    @Bean
    public NewTopic shippingUpdatedTopic() {
        return new NewTopic(KafkaTopics.SHIPMENT_UPDATED, 2, (short) 1); // 2 partitions, 1 replica
    }
}

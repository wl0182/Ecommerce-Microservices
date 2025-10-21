package com.wassimlagnaoui.ecommerce.Shipping_Service.Configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfiguration {

    @Bean
    public NewTopic shippingCreatedTopic() {
        return new NewTopic("shipping-created", 1, (short) 1); // 2 partitions, 1 replica
    }

    @Bean
    public NewTopic shippingUpdatedTopic() {
        return new NewTopic("shipment-updated", 2, (short) 1); // 2 partitions, 1 replica
    }
}

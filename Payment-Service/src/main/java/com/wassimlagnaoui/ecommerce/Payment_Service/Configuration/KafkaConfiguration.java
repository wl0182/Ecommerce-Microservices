package com.wassimlagnaoui.ecommerce.Payment_Service.Configuration;

import com.wassimlagnaoui.common_events.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfiguration {

    @Bean
    public NewTopic paymentProcessedTopic() {
        return new NewTopic(KafkaTopics.PAYMENT_PROCESSED, 2, (short) 1); // 3 partitions, 1 replica
    }
    @Bean
    public NewTopic paymentRefundedTopic() {
        return new NewTopic(KafkaTopics.PAYMENT_REFUNDED, 1, (short) 1); // 3 partitions, 1 replica
    }

    @Bean
    public NewTopic orderPaidTopic() {
        return new NewTopic("order-paid", 2, (short) 1); // 3 partitions, 1 replica
    }
}

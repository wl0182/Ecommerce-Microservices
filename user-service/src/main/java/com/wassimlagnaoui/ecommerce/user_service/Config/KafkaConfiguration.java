package com.wassimlagnaoui.ecommerce.user_service.Config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class KafkaConfiguration {
    // new topic bean
    @Bean
    public NewTopic userCreatedTopic() {
        return new NewTopic("user-created", 1, (short) 1);
    }
}

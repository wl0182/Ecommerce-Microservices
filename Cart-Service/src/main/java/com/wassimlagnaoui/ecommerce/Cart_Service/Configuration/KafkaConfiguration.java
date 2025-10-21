package com.wassimlagnaoui.ecommerce.Cart_Service.Configuration;


import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfiguration {
    // Topic bean definitions can be added here if needed
    @Bean
    public NewTopic cartClearedTopic() {
        return new NewTopic("cart-cleared", 1, (short) 1);
    }



}

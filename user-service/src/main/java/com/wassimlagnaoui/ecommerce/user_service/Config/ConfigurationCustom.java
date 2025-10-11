package com.wassimlagnaoui.ecommerce.user_service.Config;


import lombok.CustomLog;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ConfigurationCustom {
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Rest Template Bean
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

package com.wassimlagnaoui.ecommerce.Shipping_Service.Configuration;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestConfiguration {
    // Configuration class for RestTemplate bean can be added here if needed
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    @LoadBalanced
    public RestClient.Builder restClientBuilder() {
        // timeout configuration here
        var httpClient = java.net.http.HttpClient.newBuilder()
                .connectTimeout(java.time.Duration.ofSeconds(5))
                .build();

        var requestFactory = new org.springframework.http.client.JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(java.time.Duration.ofSeconds(5));

        return RestClient.builder()
                .requestFactory(requestFactory);

    } // this bean creates a RestClient.Builder with custom timeout settings, which can be used to create RestClient instances

    @Bean
    public RestClient restClient(RestClient.Builder builder) {
        return builder.build(); // using the builder to create the RestClient bean
    }



}

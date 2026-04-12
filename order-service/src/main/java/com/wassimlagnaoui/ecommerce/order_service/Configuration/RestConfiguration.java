package com.wassimlagnaoui.ecommerce.order_service.Configuration;


import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class RestConfiguration {
    // Configuration class for RestTemplate bean can be added here if needed
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
       // add timeout settings
        return new RestTemplate();
    }


   @Bean
   @LoadBalanced
   public RestClient.Builder restClientBuilder() {

       var httpClient = HttpClient.newBuilder()
               .connectTimeout(Duration.ofSeconds(5))
               .build();

       var requestFactory = new JdkClientHttpRequestFactory(httpClient);
       requestFactory.setReadTimeout(Duration.ofSeconds(5));

       return RestClient.builder()
               .requestFactory(requestFactory);
   }

    @Bean
    public RestClient restClient(RestClient.Builder builder) {
        return builder.build();
    }




}

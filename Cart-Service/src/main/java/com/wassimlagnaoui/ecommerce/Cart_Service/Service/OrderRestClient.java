package com.wassimlagnaoui.ecommerce.Cart_Service.Service;


import com.wassimlagnaoui.ecommerce.Cart_Service.DTO.RestDTOs.OrderCreatedResponse;
import com.wassimlagnaoui.ecommerce.Cart_Service.Exception.OrderServiceDownException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@Service
public class OrderRestClient {
    // Inject RestClient bean

    @Value("${services.order-service.url}")
    private String orderServiceBaseUrl;

    private final RestClient restClient;

    public OrderRestClient(RestClient restClient) {
        this.restClient = restClient;
    }

    // method to call /place-order endpoint of Order Service
    @CircuitBreaker(name = "orderServiceCircuitBreaker", fallbackMethod = "placeOrderFallback")
    public OrderCreatedResponse placeOrder(Long userId, Object createOrderDTO) {
        // Call the Order Service's /place-order endpoint using RestClient
        String url = orderServiceBaseUrl + "/orders/place-order?userId=" + userId;
        OrderCreatedResponse orderCreatedResponse = restClient.post().uri(url).body(createOrderDTO).retrieve().onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
            log.error("Client error when placing order for user {}: {}", userId, response.getStatusCode());
            throw new RuntimeException("Failed to place order: " + response.getStatusCode());
        }).onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
            log.error("Server error when placing order for user {}: {}", userId, response.getStatusCode());
            throw new OrderServiceDownException("Order Service is currently unavailable");
        }).body(OrderCreatedResponse.class);

        return orderCreatedResponse;
    }

    // Fallback method for placeOrder
    public OrderCreatedResponse placeOrderFallback(Long userId, Object createOrderDTO, Throwable throwable) {
        // do not Send an Exception here, just log the error and return a default response to indicate OrderService circuit is open
        log.warn("Failed to place order for user {}: {}", userId, throwable.getMessage());
        return OrderCreatedResponse.builder().id(-1L).userId(userId).items(List.of()).totalAmount(0.0).status("Order Service Unavailable").build();
    }


}

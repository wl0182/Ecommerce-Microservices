package com.wassimlagnaoui.ecommerce.Cart_Service.Service;


import ch.qos.logback.core.joran.conditional.IfAction;
import com.wassimlagnaoui.ecommerce.Cart_Service.DTO.RestDTOs.CreateOrderDTO;
import com.wassimlagnaoui.ecommerce.Cart_Service.DTO.RestDTOs.OrderCreatedResponse;
import com.wassimlagnaoui.ecommerce.Cart_Service.Exception.InvalidAddress;
import com.wassimlagnaoui.ecommerce.Cart_Service.Exception.OrderServiceDownException;
import com.wassimlagnaoui.ecommerce.Cart_Service.Exception.OrderServiceUnknownResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
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
    public OrderCreatedResponse placeOrder(Long userId, CreateOrderDTO createOrderDTO) {

        String url = orderServiceBaseUrl + "/orders/place-order?userId=" + userId;
        return restClient.post()
                .uri(url)
                .body(createOrderDTO)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError , (request, response) -> {
                    // handle 4xx errors
                    log.error("Client error when placing order for user {}: {}", userId, response.getStatusCode());
                })
                .body(OrderCreatedResponse.class);

    }

    // Fallback method for placeOrder
    public OrderCreatedResponse placeOrderFallback(Long userId, CreateOrderDTO createOrderDTO, Throwable throwable) {


        log.warn("Failed to place order for user {}: {}", userId, throwable.getMessage());
        return OrderCreatedResponse.builder().id(-1L).userId(userId).items(List.of()).totalAmount(0.0).status("FAILED").build();
    }


}

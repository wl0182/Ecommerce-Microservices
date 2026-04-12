package com.wassimlagnaoui.ecommerce.order_service.Service;

import com.wassimlagnaoui.ecommerce.order_service.Exception.UserServiceError;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
public class UserRestClient {
    private final RestClient restClient;

    public UserRestClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Value("${services.user-service.url}")
    private String userServiceBaseUrl;

    // This method to validate User Address
    @CircuitBreaker(name = "user-service", fallbackMethod = "validateUserAddressFallback")
    public Boolean validateUserAddress(Long userId, Long addressId) {
        String url = userServiceBaseUrl + "/api/users/" + userId + "/addresses/" + addressId+"/validate";
        return restClient.get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request,response) -> {
                    log.error("Address with id {} not found for user with id {}", addressId, userId);
                    log.error("Response status code: {}", response.getStatusCode());
                    throw new UserServiceError("Failed to validate user address with id: " + addressId + " for user with id: " + userId);
                }).body(Boolean.class);
    }
    // Fallback method for validateUserAddress
    public Boolean validateUserAddressFallback(Long userId, Long addressId, Throwable throwable) {
        log.error("User Service is unavailable. Failed to validate user address with id: {} for user with id: {}. Error: {}", addressId, userId, throwable.getMessage());
        // Return a default value or throw a custom exception
        return false; // Assuming false means the address is not valid when the service is down
    }
}

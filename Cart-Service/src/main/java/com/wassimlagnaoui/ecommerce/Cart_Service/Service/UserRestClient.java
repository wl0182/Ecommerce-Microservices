package com.wassimlagnaoui.ecommerce.Cart_Service.Service;

import com.wassimlagnaoui.ecommerce.Cart_Service.DTO.RestDTOs.UserDetails;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@Service
public class UserRestClient {
    private final RestClient  restClient;

    public UserRestClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Value("${services.user-service.url}")
    private String userServiceUrl;


    @CircuitBreaker(name = "userServiceCircuitBreaker", fallbackMethod = "fetchUserDetailsFallback")
    public UserDetails fetchUserDetails(long userId) {
        String url = userServiceUrl + "/api/users/" + userId;
        UserDetails userDetails = restClient.get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request,response) -> {
                    log.error("Client error while fetching user details for userId {}: {}", userId, response.getStatusCode());
                })
                .body(UserDetails.class);
        return userDetails;
    }

    public UserDetails fetchUserDetailsFallback(long userId, Throwable throwable) {
        log.error("Failed to fetch user details for userId {}: {}", userId, throwable.getMessage());
        // Return a default UserDetails object or null based on your application's needs
        return UserDetails.builder()
                .id(-1L)
                .email("unknown")
                .name("Unknown User")
                .phoneNumber("0000000000")
                .build();
    }
}

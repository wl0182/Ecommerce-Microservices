package com.wassimlagnaoui.ecommerce.Shipping_Service.Service;

import com.wassimlagnaoui.ecommerce.Shipping_Service.Exception.InvalidAddressValidation;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
public class UserRestClient {
    //this class will be responsible for making rest call to user-service
    private final RestClient restClient;

    public UserRestClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Value("${services.user-service.url}")
    String userServiceUrl;

    // validate user Address
    @CircuitBreaker(name = " userServiceCircuitBreaker" , fallbackMethod = "validateUserAddressFallback")
    public boolean validateUserAddress(Long userId,Long addressId) {
        Boolean result = restClient.get()
                .uri(userServiceUrl + "/api/users/" + userId + "/addresses/"+addressId+"/validate") // ("/{userId}/addresses/{addressId}/validate")
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request,response)->{
                    throw new InvalidAddressValidation("Failed to validate user address for userId: " + userId);
                })
                .body(Boolean.class);
        return result;
    }

    // fallback method for validateUserAddress
    public boolean validateUserAddressFallback(Long userId, Throwable throwable) {
        // log the error
        log.warn("User service is down. Falling back to default address validation for userId: {}. Error: {}", userId, throwable.getMessage());
        // return a default value (e.g., false) or implement a fallback logic
        return false; // assuming we consider the address invalid if the user service is down
    }
}

package com.wassimlagnaoui.ecommerce.Cart_Service.Service;

import com.wassimlagnaoui.ecommerce.Cart_Service.DTO.RestDTOs.InventoryDTO;
import com.wassimlagnaoui.ecommerce.Cart_Service.DTO.RestDTOs.ProductDTO;
import com.wassimlagnaoui.ecommerce.Cart_Service.Exception.ProductNotFoundException;
import com.wassimlagnaoui.ecommerce.Cart_Service.Exception.ProductServiceUnavailble;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class ProductRestClient {
    // inject RestClient bean to call product service

    private final RestClient restClient;

    @Value("${services.product-service.url}")
    private String productServiceUrl;

    public ProductRestClient(RestClient restClient) {
        this.restClient = restClient;
    }


    @Retry(name = "productServiceRetry")
    @CircuitBreaker(name = "productServiceCircuitBreaker", fallbackMethod = "getProductByIdFallback")
    public ProductDTO getProductById(Long productId) {
        String url = productServiceUrl + "/products/" + productId;
        ProductDTO product = restClient.get()
                .uri(url)
                .retrieve()
                .onStatus(httpStatusCode -> httpStatusCode.equals(404) , (request, response)-> {
                    // handle 404 error
                    log.error("Product with id {} not found: {}", productId, response.getStatusCode());
                    throw new ProductNotFoundException("Product not found with id: " + productId);
                }) // handle 404 errors
                .onStatus(HttpStatusCode::is4xxClientError, (request,response) -> {
                    // handle 4xx errors
                    log.error("Client error when fetching product with id {}: {}", productId, response.getStatusCode());
                    throw new ProductNotFoundException("Product Service 400 response " + productId);
                }).onStatus(HttpStatusCode::is5xxServerError, (request,response) -> {
                    // handle 5xx errors
                    log.error("Server error when fetching product with id {}: {}", productId, response.getStatusCode());
                    throw new ProductServiceUnavailble("Product Service is currently unavailable");
                })
                .body(ProductDTO.class);
        return product;
    }

    // get bulk Products by ids
    @Retry(name = "productServiceRetry")
    @CircuitBreaker(name = "productServiceCircuitBreaker", fallbackMethod = "getProductsByIdsFallback")
    public List<ProductDTO> getProductsByIds(List<Long> productIds) {
        String url = productServiceUrl + "/products/bulk" ;
        ProductDTO[] products = restClient.post()
                .uri(url)
                .body(productIds)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,(request, response) -> {
                    // handle client error
                    log.error("Client error when fetching products by ids: {}", productIds);
                    throw new ProductNotFoundException("Products not found for ids: " + productIds);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request,response) -> {
                    // handle server error
                    log.error("Server error when fetching products by ids: {}", productIds);
                    throw new ProductServiceUnavailble("Product Service is currently unavailable");
                })
                .body(ProductDTO[].class);
        // check if products is null or empty
        if (products == null ) {
            throw new ProductServiceUnavailble("Product Service is currently unavailable");
        }
        return Arrays.asList(products);
    }


    @Retry(name = "productServiceRetry")
    @CircuitBreaker(name = "productServiceCircuitBreaker", fallbackMethod = "getProductInventoryByIdFallback")
    public InventoryDTO getProductInventoryById(Long productId) {
        String url = productServiceUrl + "/products/" + productId + "/inventory";
        InventoryDTO inventory = restClient.get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request,response) -> {
                    // handle 4xx errors
                    log.error("Client error when fetching inventory for product with id {}: {}", productId, response.getStatusCode());
                    throw new ProductNotFoundException("Product not found with id: " + productId);
                }).onStatus(HttpStatusCode::is5xxServerError, (request,response) -> {
                    // handle 5xx errors
                    log.error("Server error when fetching inventory for product with id {}: {}", productId, response.getStatusCode());
                    throw new ProductServiceUnavailble("Product Service is currently unavailable");
                })
                .body(InventoryDTO.class);

        log.info("Inventory for product id {}: {}", productId, inventory);
        return inventory;
    }

    // Fallback methods for circuit breaker
    public ProductDTO getProductByIdFallback(Long productId, Throwable ex) {
       log.warn("Fallback From Circuit Breaker for getProductById with id {}: {}", productId, ex.getMessage());
        // do not throw exception here, just return null to indicate product is not available
        return ProductDTO.builder()
                .id(productId)
                .name("Unknown Product")
                .description("Product information is currently unavailable")
                .sku("unknown")
                .categoryName("unknown")
                .price(BigDecimal.ZERO)
                .build();
    }

    // Fallback for bulk products
    public List<ProductDTO> getProductsByIdsFallback(List<Long> productIds, Throwable ex) {
         log.warn("Fallback From Circuit Breaker for getProductsByIds with ids {}: {}", productIds, ex.getMessage());
        return productIds.stream().map(id -> ProductDTO.builder()
                .id(id)
                .name("Unknown Product")
                .description("Product information is currently unavailable")
                .sku("unknown")
                .categoryName("unknown")
                .price(BigDecimal.ZERO)
                .build()).toList();
    }

    public InventoryDTO getProductInventoryByIdFallback(Long productId, Throwable ex) {
        log.warn("Fallback From Circuit Breaker for getProductInventoryById with id {}: {}", productId, ex.getMessage());
        return InventoryDTO.builder()
                .productId(productId)
                .productName("Unknown Product")
                .sku("unknown")
                .stockInventory(0)
                .build();
    }









}

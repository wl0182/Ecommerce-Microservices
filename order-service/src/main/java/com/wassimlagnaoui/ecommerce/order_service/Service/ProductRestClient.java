package com.wassimlagnaoui.ecommerce.order_service.Service;


import com.wassimlagnaoui.ecommerce.order_service.DTO.ProductDTO;
import com.wassimlagnaoui.ecommerce.order_service.Exception.ProductServiceError;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@Slf4j
@Service
public class ProductRestClient {
    private final RestClient restClient;
    public ProductRestClient(RestClient restClient) {
        this.restClient = restClient;
    }

    // Properties
    @Value("${services.product-service.url}")
    private String productServiceUrl;

    // Method to fetch product details by ID
    @CircuitBreaker(name = "product-service",fallbackMethod = "getProductByIdFallback")
    public ProductDTO getProductById(Long productId) {
        String url = productServiceUrl + "/products/" + productId;
        return restClient.get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request,response) -> {
                    log.error("Failed to fetch product with ID {} from Product Service. Status code: {}", productId, response.getStatusCode());
                    throw new ProductServiceError("Failed to fetch product with ID " + productId + " from Product Service. Status code: " + response.getStatusCode());
                })
                .body(ProductDTO.class);
    }

    public ProductDTO getProductByIdFallback(Long productId,Throwable throwable) {
        return ProductDTO.builder().id(productId).name("Unknown Product").price(BigDecimal.ZERO).sku("Unknown Sku").description("Description").build();
    }

    // Get Bulk Products by IDs
    @CircuitBreaker(name = "product-service",fallbackMethod = "getProductsByIdsFallback")
    public List<ProductDTO> getProductsByIds(List<Long> productIds) {
        String url = productServiceUrl + "/products/bulk" ;
        ProductDTO[] productDTOS = restClient.post()
                .uri(url)
                .body(productIds)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request,response) -> {
                    log.error("Failed to fetch products from Product Service. Status code: {}", response.getStatusCode());
                    throw new ProductServiceError("Failed to fetch products from Product Service. Status code: " + response.getStatusCode());
                })
                .body(ProductDTO[].class);

        return List.of(productDTOS);
    }

    public ProductDTO[] getProductsByIdsFallback(Long[] productIds,Throwable throwable) {
        log.warn("Fallback triggered for getProductsByIds due to: {}", throwable.getMessage());
        ProductDTO[] fallbackProducts = new ProductDTO[productIds.length];
        for (int i = 0; i < productIds.length; i++) {
            fallbackProducts[i] = ProductDTO.builder().id(productIds[i]).name("Unknown Product").price(BigDecimal.ZERO).sku("Unknown Sku").description("Description").build();
        }
        return fallbackProducts;
    }

}

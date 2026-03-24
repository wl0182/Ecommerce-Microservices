package com.wassimlagnaoui.ecommerce.Cart_Service.Service;

import com.wassimlagnaoui.ecommerce.Cart_Service.DTO.RestDTOs.InventoryDTO;
import com.wassimlagnaoui.ecommerce.Cart_Service.DTO.RestDTOs.ProductDTO;
import com.wassimlagnaoui.ecommerce.Cart_Service.Exception.ProductNotFoundException;
import com.wassimlagnaoui.ecommerce.Cart_Service.Exception.ProductServiceUnavailble;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestClient;

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


    public ProductDTO getProductById(Long productId) {
        String url = productServiceUrl + "/products/" + productId;
        ProductDTO product = restClient.get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request,response) -> {
                    // handle 4xx errors
                    throw new ProductNotFoundException("Product not found with id: " + productId);
                }).onStatus(HttpStatusCode::is5xxServerError, (request,response) -> {
                    // handle 5xx errors
                    throw new ProductServiceUnavailble("Product Service is currently unavailable");
                })
                .body(ProductDTO.class);
        return product;
    }

    // get bulk Products by ids
    public List<ProductDTO> getProductsByIds(List<Long> productIds) {
        String url = productServiceUrl + "/products/bulk" ;
        ProductDTO[] products = restClient.post()
                .uri(url)
                .body(productIds)
                .retrieve()
                .body(ProductDTO[].class);
        // check if products is null or empty
        if (products == null || products.length == 0) {
            throw new RuntimeException("Products not found for ids: " + productIds);
        }
        return Arrays.asList(products);
    }


    public InventoryDTO getProductInventoryById(Long productId) {
        String url = productServiceUrl + "/products/" + productId + "/inventory";
        InventoryDTO inventory = restClient.get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request,response) -> {
                    // handle 4xx errors
                    throw new ProductNotFoundException("Product not found with id: " + productId);
                }).onStatus(HttpStatusCode::is5xxServerError, (request,response) -> {
                    // handle 5xx errors
                    throw new ProductServiceUnavailble("Product Service is currently unavailable");
                })
                .body(InventoryDTO.class);

        log.info("Inventory for product id {}: {}", productId, inventory);
        return inventory;
    }









}

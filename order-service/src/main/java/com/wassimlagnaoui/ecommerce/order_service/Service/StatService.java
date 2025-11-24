package com.wassimlagnaoui.ecommerce.order_service.Service;


import com.wassimlagnaoui.ecommerce.order_service.DTO.ProductDTO;
import com.wassimlagnaoui.ecommerce.order_service.DTO.TopProductsResponse;
import com.wassimlagnaoui.ecommerce.order_service.Repository.OrderItemRepository;
import com.wassimlagnaoui.ecommerce.order_service.Repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class StatService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;


    @Value("${services.product-service.url}")
    private String productServiceUrl;

    @Autowired
    private RestTemplate restTemplate;

    public StatService(OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }


    // top 5 best-selling products
    public List<TopProductsResponse> getTopSellingProducts() {
        List<Object[]> results = orderRepository.findTop5OrderedProducts();

        List<TopProductsResponse> topProducts = results.stream().map(record -> {
            Long productId = ((Number) record[0]).longValue();
            Long totalSold = ((Number) record[1]).longValue();
            // Fetch product name from OrderItemRepository
            return TopProductsResponse.builder()
                    .productId(productId)
                    .totalSold(totalSold)
                    .build();
        }).toList();

        List<Long> productIds = topProducts.stream()
                .map(TopProductsResponse::getProductId)
                .toList();

        List<ProductDTO> products = getProductsByIds(productIds);

        // Map product names to topProducts
        for (TopProductsResponse topProduct : topProducts) {
            products.stream()
                    .filter(p -> p.getId().equals(topProduct.getProductId()))
                    .findFirst()
                    .ifPresent(p -> topProduct.setName(p.getName()));
        }


        return topProducts;
    }



    @CircuitBreaker(name = "product-service", fallbackMethod = "getProductsByIdFallback")
    public List<ProductDTO> getProductsByIds(List<Long> productIds) {
        String url = productServiceUrl + "/products/bulk" ;
        ResponseEntity<ProductDTO[]> response = restTemplate.postForEntity(url,productIds,ProductDTO[].class);

        return Arrays.asList(response.getBody());
    }

    // Fallback method
    public List<ProductDTO> getProductsByIdFallback(List<Long> productIds, Throwable throwable) {
        // Return empty list or cached data
        return List.of();

    }



}

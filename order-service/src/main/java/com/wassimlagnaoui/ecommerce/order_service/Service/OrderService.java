package com.wassimlagnaoui.ecommerce.order_service.Service;

import com.wassimlagnaoui.ecommerce.order_service.DTO.*;
import com.wassimlagnaoui.ecommerce.order_service.Entities.Order;
import com.wassimlagnaoui.ecommerce.order_service.Entities.OrderItem;
import com.wassimlagnaoui.ecommerce.order_service.Entities.OrderStatus;
import com.wassimlagnaoui.ecommerce.order_service.Exception.OrderNotFound;
import com.wassimlagnaoui.ecommerce.order_service.Repository.OrderItemRepository;
import com.wassimlagnaoui.ecommerce.order_service.Repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;


    // adding RestTemplate to call other services
    @Autowired
    private RestTemplate restTemplate;


    @Value("${services.product-service.url}")
    private String productServiceUrl;


    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;

    }

    @Transactional
    public OrderCreatedResponse createOrder(Long userId, CreateOrderDTO createOrderDTO){
        Order order = new Order();
        List<OrderItem> orderItems = new ArrayList<>();

        order.setUserId(userId);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setLastUpdated(LocalDateTime.now());
        double totalAmount = 0.0;

        // get Product ids from createOrderDTO
        List<Long> productIds = extractProductIds(createOrderDTO.getItems());

        // get product details by list of ids
        List<ProductDTO> products = getProductsByIds(productIds);

        // map products by id
        HashMap<Long,ProductDTO> productMap = mapProductsById(products);

        for (OrderItemRequest itemRequest : createOrderDTO.getItems()) {
            ProductDTO product = productMap.get(itemRequest.getProductId());
            if (product == null) {
                throw new RuntimeException("Product with id " + itemRequest.getProductId() + " not found");
            }
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(itemRequest.getProductId());
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPrice(BigDecimal.valueOf(product.getPrice()));
            orderItem.setOrder(order);

            orderItems.add(orderItem);
            totalAmount += product.getPrice() * itemRequest.getQuantity();
        }

        order.setOrderItems(orderItems);
        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);
        orderItemRepository.saveAll(orderItems);

        List<OrderItemResponse> itemResponses = orderItems.stream().map(oi -> {
            ProductDTO product = productMap.get(oi.getProductId());
            return OrderItemResponse.builder()
                    .productId(oi.getProductId())
                    .productName(product != null ? product.getName() : "Unknown Product")
                    .quantity(oi.getQuantity())
                    .price(oi.getPrice())
                    .productDescription(product != null ? product.getDescription() : "No description available")
                    .productCategory(product != null ? product.getCategoryName() : "Uncategorized")
                    .build();
        }).collect(Collectors.toList());




        return OrderCreatedResponse.builder()
                .id(savedOrder.getId())
                .userId(savedOrder.getUserId())
                .items(itemResponses)
                .totalAmount(savedOrder.getTotalAmount())
                .status(savedOrder.getStatus().name())
                .createdAt(savedOrder.getOrderDate().toString())
                .build(); // { id, userId, items:[{ productId, name, quantity, price }], totalAmount, status, createdAt, updatedAt }
    }



    public OrderDTO getOrderById(Long orderId) {

        return null;
    } 
    
    // get orders by user id Alternative implementation without Product Name
    public List<OrdersForUserResponse> getOrdersTotalByUsers(Long userId){
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream().map(order -> {
            OrdersForUserResponse response = new OrdersForUserResponse();
            response.setId(order.getId());
            response.setTotalAmount(order.getTotalAmount());
            response.setStatus(order.getStatus().name());
            response.setCreatedAt(order.getOrderDate().toString());
            return response;
        }).toList();  // list of // [{ id, totalAmount, status, createdAt }]
    }

    // ordered Cancelled
    public OrderCancelled cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFound("Order with id " + orderId + " not found"));
        order.setStatus(OrderStatus.CANCELED);
        order.setLastUpdated(LocalDateTime.now());
        Order updatedOrder = orderRepository.save(order);
        return OrderCancelled.builder()
                .id(updatedOrder.getId())
                .status(updatedOrder.getStatus().name())
                .updatedAt(updatedOrder.getLastUpdated().toString())
                .build(); // { id, status, updatedAt }
    }
    // get status of an order by order id
    @Transactional(readOnly = true)
    public OrderStatusDTO getOrderStatus(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFound("Order with id " + orderId + " not found"));
        return OrderStatusDTO.builder()
                .id(order.getId())
                .status(order.getStatus().name())
                .build(); // { id, status }
    }


    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByUserId(Long userId) {
        List<Order> orders = orderRepository.findOrderByUserIdWithItems(userId);

        if (orders.isEmpty()){
            throw new OrderNotFound("No orders found for user with id " + userId);
        }

        List<OrderItem> items = orders.stream()
                .flatMap(order -> order.getOrderItems().stream())
                .collect(Collectors.toList());

        List<Long> productIds = items.stream()
                .map(OrderItem::getProductId)
                .distinct()
                .collect(Collectors.toList());

        // Fetch product details
        List<ProductDTO> products = getProductsByIds(productIds);
        HashMap<Long, ProductDTO> productMap = mapProductsById(products);


        List<OrderItemResponse> orderItemResponses = items.stream()
                .map(item -> {
                    ProductDTO product = productMap.get(item.getProductId());
                    return OrderItemResponse.builder()
                            .productId(item.getProductId())
                            .productName(product != null ? product.getName() : "Unknown Product")
                            .quantity(item.getQuantity())
                            .price(item.getPrice())
                            .productDescription(product != null ? product.getDescription() : "No description available")
                            .productCategory(product != null ? product.getCategoryName() : "Uncategorized")
                            .build();
                })
                .collect(Collectors.toList());


        List<OrderDTO> orderDTOS = orders.stream().map(order -> {
            List<OrderItemResponse> itemsForOrder = orderItemResponses.stream()
                    .filter(itemResponse -> {
                        for (OrderItem item : order.getOrderItems()) {
                            if (item.getProductId().equals(itemResponse.getProductId())
                                    && item.getQuantity() == itemResponse.getQuantity()
                                    && item.getPrice().equals(itemResponse.getPrice())) {
                                return true;
                            }
                        }
                        return false;
                    })
                    .collect(Collectors.toList());

            return OrderDTO.builder()
                    .id(order.getId())
                    .userId(order.getUserId())
                    .items(itemsForOrder)
                    .totalAmount(order.getTotalAmount())
                    .status(order.getStatus().name())
                    .createdAt(order.getOrderDate())
                    .updatedAt(order.getLastUpdated())
                    .build();
        }).collect(Collectors.toList());


        return orderDTOS;
    // List of { id, userId, items:[{ productId,  ProductName, quantity, price }], totalAmount, status, createdAt, updatedAt }
    }

    public List<ProductDTO> getProductsByIdsService(List<Long> productIds) {
        return getProductsByIds(productIds);
    }

    public ProductDTO getProductByIdService(Long productId) {
        return getProductById(productId);
    }


    @CircuitBreaker(name = "productServiceCircuitBreaker", fallbackMethod = "getProductByIdFallback")
    private ProductDTO getProductById(Long productId) {
        String url = productServiceUrl + "/products/" + productId;
        return restTemplate.getForObject(url, ProductDTO.class);
    }

    @CircuitBreaker(name = "productServiceCircuitBreaker", fallbackMethod = "getProductsByIdFallback")
    private List<ProductDTO> getProductsByIds(List<Long> productIds) {
        String url = productServiceUrl + "/products/bulk" ;
        ResponseEntity<ProductDTO[]> response = restTemplate.postForEntity(url,productIds,ProductDTO[].class);
        return List.of(response.getBody());
    }



    private ProductDTO getProductByIdFallback(Long productId, Throwable throwable) {
       log.info("Fallback executed for getProductById with productId: " + productId + " due to: " + throwable.getMessage());
        return ProductDTO.builder()
                .id(productId)
                .name("Unknown Product")
                .description("Product information is currently unavailable.")
                .price(0.0)
                .build();
    }

    private List<ProductDTO> getProductsByIdFallback(List<Long> productIds, Throwable throwable) {
        log.info("Fallback executed for getProductsByIds with productIds: " + productIds + " due to: " + throwable.getMessage());
        List<ProductDTO> fallbackProducts = new ArrayList<>();
        for (Long productId : productIds) {
            fallbackProducts.add(ProductDTO.builder()
                    .id(productId)
                    .name("Unknown Product")
                    .description("Product information is currently unavailable.")
                    .price(0.0)
                    .build());
        }
        return fallbackProducts;

    }

    private HashMap<Long,ProductDTO> mapProductsById(List<ProductDTO> products){
        HashMap<Long,ProductDTO> productMap = new HashMap<>();
        for (ProductDTO product : products) {
            productMap.put(product.getId(), product);
        }
        return productMap;
    }

    private List<Long> extractProductIds(List<OrderItemRequest> orderItems){
        return orderItems.stream()
                .map(OrderItemRequest::getProductId)
                .collect(Collectors.toList());
    }


}

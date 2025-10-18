package com.wassimlagnaoui.ecommerce.order_service.Service;

import com.wassimlagnaoui.ecommerce.order_service.DTO.*;
import com.wassimlagnaoui.ecommerce.order_service.Entities.Order;
import com.wassimlagnaoui.ecommerce.order_service.Entities.OrderItem;
import com.wassimlagnaoui.ecommerce.order_service.Entities.OrderStatus;
import com.wassimlagnaoui.ecommerce.order_service.Exception.OrderNotFound;
import com.wassimlagnaoui.ecommerce.order_service.Repository.OrderItemRepository;
import com.wassimlagnaoui.ecommerce.order_service.Repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Fallback;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;




    // adding RestTemplate to call other services
    @Autowired
    private RestTemplate restTemplate;


    @Value("${product.service.url}")
    private String productServiceUrl;





    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;

    }

    @Transactional
    public OrderCreatedResponse createOrder(Long userId, CreateOrderDTO createOrderDTO){
        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());

        // retrieve items from createOrderDTO and set them to order
        List<OrderItem> orderItems = createOrderDTO.getItems().stream()
                .map(orderItemDTO -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setProductId(orderItemDTO.getProductId());
                    orderItem.setQuantity(orderItemDTO.getQuantity());
                    orderItem.setPrice(orderItemDTO.getPrice());
                    return orderItem;
                }).collect(Collectors.toUnmodifiableList());

        order.setOrderItems(orderItems);

        Double totalAmount = orderItems.stream().mapToDouble(value -> value.getPrice()*value.getQuantity()).sum();

        Order savedOrder = orderRepository.save(order);

        return OrderCreatedResponse.builder()
                .id(savedOrder.getId())
                .items(createOrderDTO.getItems())
                .status(order.getStatus().name())
                .totalAmount(totalAmount)
                .userId(userId)
                .createdAt(LocalDateTime.now().toString())
                .build();

    }



    public OrderDTO getOrderById(Long orderId) {
        // Implementation

        Order order = orderRepository.findById(orderId).orElseThrow(()-> new OrderNotFound("Order with id "+ orderId +" not found"));

        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);

        List<OrderItemDTO> item = new ArrayList<>();

        // get product id for each orderItem
        List<Long> productIds = orderItems.stream().map(OrderItem::getProductId).collect(Collectors.toList());

        List<ProductDTO> products = getProductsByIds(productIds);

        // create a map of product id to productDTO for easy lookup
        Map<Long,ProductDTO> productMap = products.stream().collect(Collectors.toMap(ProductDTO::getId,productDTO -> productDTO));

        for (OrderItem orderItem : orderItems) {
            ProductDTO product = productMap.get(orderItem.getProductId());
            if (product != null) {
                OrderItemDTO orderItemDTO = new OrderItemDTO();
                orderItemDTO.setProductId(product.getId());
                orderItemDTO.setProductName(product.getName());
                orderItemDTO.setQuantity(orderItem.getQuantity());
                orderItemDTO.setPrice(orderItem.getPrice());
                item.add(orderItemDTO);
            }
        }


        return OrderDTO.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .items(item)
                .totalAmount(orderItems.stream().mapToDouble(value -> value.getPrice()*value.getQuantity()).sum())
                .status(order.getStatus().name())
                .createdAt(order.getOrderDate())
                .updatedAt(order.getLastUpdated())
                .build();  // { id, userId, items:[{ productId, productName ,quantity, price }], totalAmount, status, createdAt, updatedAt }
    }

    public List<OrderDTO> getOrdersByUserId(Long userId) {
        // Implementation
        return null; // List of { id, userId, items:[{ productId,  ProductName, quantity, price }], totalAmount, status, createdAt, updatedAt }
    }


    @CircuitBreaker(name = "productServiceCircuitBreaker", fallbackMethod = "getProductByIdFallback")
    private ProductDTO getProductById(Long productId) {
        String url = productServiceUrl + "/products/" + productId;
        return restTemplate.getForObject(url, ProductDTO.class);
    }

    private List<ProductDTO> getProductsByIds(List<Long> productIds) {
        String url = productServiceUrl + "/bulk" ;
        ResponseEntity<ProductDTO[]> response = restTemplate.postForEntity(url,productIds,ProductDTO[].class);
        return List.of(response.getBody());
    }


    private ProductDTO getProductByIdFallback(Long productId, Throwable throwable) {
        return ProductDTO.builder()
                .id(productId)
                .name("Unknown Product")
                .description("Product information is currently unavailable.")
                .price(0.0)
                .build();

    }


}

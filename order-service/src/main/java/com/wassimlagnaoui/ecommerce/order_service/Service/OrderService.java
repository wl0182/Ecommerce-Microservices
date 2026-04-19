package com.wassimlagnaoui.ecommerce.order_service.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wassimlagnaoui.common_events.Events.OrderService.OrderCreateEvent;
import com.wassimlagnaoui.common_events.KafkaTopics;
import com.wassimlagnaoui.ecommerce.order_service.DTO.*;
import com.wassimlagnaoui.ecommerce.order_service.Entities.*;
import com.wassimlagnaoui.ecommerce.order_service.Exception.AddressUnavailable;
import com.wassimlagnaoui.ecommerce.order_service.Exception.OrderItemsListEmpty;
import com.wassimlagnaoui.ecommerce.order_service.Exception.OrderNotFound;
import com.wassimlagnaoui.ecommerce.order_service.Exception.UserServiceError;
import com.wassimlagnaoui.ecommerce.order_service.Repository.OrderItemRepository;
import com.wassimlagnaoui.ecommerce.order_service.Repository.OrderOutboxRepository;
import com.wassimlagnaoui.ecommerce.order_service.Repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.persistence.Convert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.client.RestTemplate;

import java.awt.print.Pageable;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderOutboxRepository orderOutboxRepository;

    private final UserRestClient userRestClient;


    @Autowired
    private ObjectMapper objectMapper;


    @Autowired
    private OrderKafkaPublisher kafkaPublisher;

    private final ProductRestClient productRestClient;


    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, OrderOutboxRepository orderOutboxRepository, UserRestClient userRestClient, ProductRestClient productRestClient) {
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.orderOutboxRepository = orderOutboxRepository;
        this.userRestClient = userRestClient;

        this.productRestClient = productRestClient;
    }

    @Transactional
    @Deprecated
    public OrderCreatedResponse createOrder(Long userId, CreateOrderDTO createOrderDTO){
        Order order = new Order();
        List<OrderItem> orderItems = new ArrayList<>();

        order.setUserId(userId);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setLastUpdated(LocalDateTime.now());
        BigDecimal totalAmount = BigDecimal.ZERO;

        // get Product ids from createOrderDTO
        List<Long> productIds = extractProductIds(createOrderDTO.getItems());

        // get product details by list of ids
        List<ProductDTO> products = productRestClient.getProductsByIds(productIds);

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
            orderItem.setPrice(product.getPrice());
            orderItem.setOrder(order);

            orderItems.add(orderItem);
            // calculate total amount
            totalAmount = totalAmount.add(orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
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

        // Publish OrderCreateEvent to Kafka // { orderId, userId, totalAmount, paymentMethod, items:[{ productId, quantity, price }], createdAt }
        OrderCreateEvent orderCreateEvent = OrderCreateEvent.builder()
                .orderId(String.valueOf(savedOrder.getId()))
                .userId(String.valueOf(savedOrder.getUserId()))
                .totalAmount(savedOrder.getTotalAmount())
                .paymentMethod(createOrderDTO.getPaymentMethod().name())
                .items(orderItems.stream().map(oi -> OrderCreateEvent.Item.builder()
                        .productId(String.valueOf(oi.getProductId()))
                        .quantity(oi.getQuantity())
                        .price(oi.getPrice()) // line 123
                        .build()).collect(Collectors.toList()))
                .createdAt(savedOrder.getOrderDate().toString())
                .build();

        // Publish the event after committing the transaction
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                kafkaPublisher.publish(KafkaTopics.ORDER_CREATED, orderCreateEvent);
            }
        });



        return OrderCreatedResponse.builder()
                .id(savedOrder.getId())
                .userId(savedOrder.getUserId())
                .items(itemResponses)
                .totalAmount(savedOrder.getTotalAmount())
                .status(savedOrder.getStatus().name())
                .createdAt(savedOrder.getOrderDate().toString())
                .build(); // { id, userId, items:[{ productId, name, quantity, price }], totalAmount, status, createdAt, updatedAt }
    }

    @Transactional
    public OrderCreatedResponse placeOrder(Long userId, CreateOrderDTO createOrderDTO) {
        // Validate if items are present in the request
        if (createOrderDTO.getItems() == null || createOrderDTO.getItems().isEmpty()) {
            log.error("Order creation failed for user {}: No items provided", userId);
            throw new OrderItemsListEmpty("Order must contain at least one item");
        }

        // validate if address id is valid
        if (!userRestClient.validateUserAddress(userId, createOrderDTO.getAddressId())) {
            log.error("Order creation failed for user {}: Invalid address id {}", userId, createOrderDTO.getAddressId());
            throw new AddressUnavailable("Invalid address id: " + createOrderDTO.getAddressId() + " for user with id: " + userId);
        }

        // Build Order and OrderItems and save to database
        Order order = new Order();
        List<OrderItem> orderItems = new ArrayList<>();
        order.setUserId(userId);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setLastUpdated(LocalDateTime.now());
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : createOrderDTO.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(itemRequest.getProductId());
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPrice(itemRequest.getPrice());
            order.addOrderItem(orderItem);
            // calculate total amount
            totalAmount = totalAmount.add(orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);

        //OrderCreateEvent to Kafka
        OrderCreateEvent orderCreateEvent = createOrderEvent(savedOrder, createOrderDTO.getPaymentMethod());
        // Publish the event after committing the transaction
        OrderOutbox orderOutbox = new OrderOutbox();
        orderOutbox.setId(UUID.randomUUID());
        orderOutbox.setAggregateId(order.getId());
        orderOutbox.setEventType(KafkaTopics.ORDER_CREATED);
        // set payload
        try {
            String payload = objectMapper.writeValueAsString(orderCreateEvent);
            orderOutbox.setPayload(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON Processing Exception Occured");
        }
        orderOutbox.setStatus(EventStatus.PENDING);

        orderOutboxRepository.save(orderOutbox);




        return OrderCreatedResponse.builder()
                .id(savedOrder.getId())
                .userId(savedOrder.getUserId())
                .totalAmount(savedOrder.getTotalAmount())
                .status(savedOrder.getStatus().name())
                .createdAt(savedOrder.getOrderDate().toString())
                .build(); // { id, userId, totalAmount, status, createdAt }

    }

    public OrderCreateEvent createOrderEvent(Order order,PaymentMethod paymentMethod){
        return OrderCreateEvent.builder()
                .orderId(String.valueOf(order.getId()))
                .userId(String.valueOf(order.getUserId()))
                .totalAmount(order.getTotalAmount())
                .paymentMethod(paymentMethod.name()) // Assuming payment method is credit card for this example
                .items(order.getOrderItems().stream().map(oi -> OrderCreateEvent.Item.builder()
                        .productId(String.valueOf(oi.getProductId()))
                        .quantity(oi.getQuantity())
                        .price(oi.getPrice())
                        .build()).collect(Collectors.toList()))
                .createdAt(order.getOrderDate().toString())
                .build();
    }


    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long orderId) {
        Order order = orderRepository.findOrderWithItems(orderId).orElseThrow(() -> new OrderNotFound("Order with id " + orderId + " not found"));

        List<OrderItem> orderItems = order.getOrderItems();
        // get product ids from order items
        List<Long> productIds = orderItems.stream()
                .map(OrderItem::getProductId)
                .distinct() // avoid duplicate ids
                .collect(Collectors.toList());


        // get product details by list of ids
        List<ProductDTO> products = productRestClient.getProductsByIds(productIds);
        HashMap<Long, ProductDTO> productMap = mapProductsById(products);


        // create order item responses with product names
        List<OrderItemResponse> items = new ArrayList<>();

        items = orderItems.stream().map(oi -> {
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

        // build and return OrderDTO

        BigDecimal totalAmount = orderItems.stream()
                .map(oi -> oi.getPrice()
                        .multiply(BigDecimal.valueOf(oi.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);



        return OrderDTO.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .items(items)
                .totalAmount(totalAmount)
                .status(order.getStatus().name())
                .createdAt(order.getOrderDate())
                .updatedAt(order.getLastUpdated())
                .build();
        // { id, userId, items:[{ productId, name, quantity, price }], totalAmount, status, createdAt, updatedAt }
    } 
    
    // get orders by user id Alternative implementation without Product Name
    public List<OrdersForUserResponse> getOrdersTotalByUsers(Long userId){
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream().map(order -> {
            OrdersForUserResponse response = new OrdersForUserResponse();
            response.setId(order.getId());
            response.setTotalAmount(order.getTotalAmount().doubleValue());
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
        List<ProductDTO> products = productRestClient.getProductsByIds(productIds);
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
        return productRestClient.getProductsByIds(productIds);
    }

    public ProductDTO getProductByIdService(Long productId) {
        return productRestClient.getProductById(productId);
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


    // update order status manually
    public OrderDTO updateOrderStatus(long orderId, OrderStatusDTO orderStatusDTO){
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFound("Order with id " + orderId + " not found"));
        order.setStatus(OrderStatus.valueOf(orderStatusDTO.getStatus()));
        order.setLastUpdated(LocalDateTime.now());
        Order updatedOrder = orderRepository.save(order);

        return OrderDTO.builder()
                .id(updatedOrder.getId())
                .userId(updatedOrder.getUserId())
                .totalAmount(updatedOrder.getTotalAmount())
                .status(updatedOrder.getStatus().name())
                .createdAt(updatedOrder.getOrderDate())
                .updatedAt(updatedOrder.getLastUpdated())
                .build();
    }

    // Get All orders Paginated
    public Page<OrderDTO> getAllOrders(int page, int size){
        PageRequest pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
        Page<Order> ordersPage = orderRepository.findAll(pageable);

        return ordersPage.map(order -> OrderDTO.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus().name())
                .createdAt(order.getOrderDate())
                .updatedAt(order.getLastUpdated())
                .build());

    }

    public List<OrderDTO> findOrderByStatus(OrderStatus status){

        List<Order> orders = orderRepository.findByStatus(status);

        if (orders.isEmpty()){
            throw new OrderNotFound("No orders found with status " + status);
        }

        return orders.stream().map(order -> OrderDTO.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus().name())
                .createdAt(order.getOrderDate())
                .updatedAt(order.getLastUpdated())
                .build()).toList();
    }


}

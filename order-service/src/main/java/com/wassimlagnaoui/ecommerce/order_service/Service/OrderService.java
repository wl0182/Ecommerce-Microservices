package com.wassimlagnaoui.ecommerce.order_service.Service;

import com.wassimlagnaoui.ecommerce.order_service.DTO.CreateOrderDTO;
import com.wassimlagnaoui.ecommerce.order_service.DTO.OrderCreatedResponse;
import com.wassimlagnaoui.ecommerce.order_service.Entities.Order;
import com.wassimlagnaoui.ecommerce.order_service.Entities.OrderItem;
import com.wassimlagnaoui.ecommerce.order_service.Entities.OrderStatus;
import com.wassimlagnaoui.ecommerce.order_service.Repository.OrderItemRepository;
import com.wassimlagnaoui.ecommerce.order_service.Repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;



    // adding RestTemplate to call other services
    @Autowired
    private RestTemplate restTemplate;


    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;

    }

    @Transactional
    public OrderCreatedResponse createOrder(Long userId, CreateOrderDTO createOrderDTO){
        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(OrderStatus.PENDING.name());
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
                .status(order.getStatus())
                .totalAmount(totalAmount)
                .userId(userId)
                .createdAt(LocalDateTime.now().toString())
                .build();

    }


}

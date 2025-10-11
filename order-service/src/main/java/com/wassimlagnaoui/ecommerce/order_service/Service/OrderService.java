package com.wassimlagnaoui.ecommerce.order_service.Service;

import com.wassimlagnaoui.ecommerce.order_service.DTO.CreateOrderDTO;
import com.wassimlagnaoui.ecommerce.order_service.DTO.OrderDetails;
import com.wassimlagnaoui.ecommerce.order_service.DTO.OrderItemDTO;
import com.wassimlagnaoui.ecommerce.order_service.DTO.UserDetails;
import com.wassimlagnaoui.ecommerce.order_service.Entities.Order;
import com.wassimlagnaoui.ecommerce.order_service.Entities.OrderItem;
import com.wassimlagnaoui.ecommerce.order_service.Repository.OrderItemRepository;
import com.wassimlagnaoui.ecommerce.order_service.Repository.OrderRepository;
import com.wassimlagnaoui.ecommerce.order_service.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;


    // adding RestTemplate to call other services
    @Autowired
    private  RestTemplate restTemplate;





    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, ProductRepository productRepository) {
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;

    }

    // Get order by id with items
    @Transactional(readOnly = true)
    public OrderDetails getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(order -> new OrderDetails(
                        order.getOrderDate().toString(),
                        order.getStatus(),
                        order.getTotalAmount(),
                        order.getUserId(),
                        orderItemRepository.findOrderItemsByOrderId(order.getId())
                ))
                .orElse(null);
    }


    // create order
    @Transactional
    public OrderDetails createOrder(CreateOrderDTO createOrderDTO) {
        Order order = new Order();
        order.setUserId(createOrderDTO.getUserId());
        order.setStatus("PENDING");
        order.setUserId(createOrderDTO.getUserId());
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(createOrderDTO.getTotalAmount());

        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemDTO orderItemDTO : createOrderDTO.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(orderItemDTO.getProductId());
            orderItem.setQuantity(orderItemDTO.getQuantity());
            orderItem.setPrice(productRepository.findById(orderItemDTO.getProductId())
                    .map(product -> product.getPrice() * orderItemDTO.getQuantity())
                    .orElse(0.0));
            orderItem.setOrder(order);
            orderItems.add(orderItem);
        }

        order.setOrderItems(orderItems);
        Order savedOrder = orderRepository.save(order);
        orderItemRepository.saveAll(orderItems);

        return new OrderDetails(
                savedOrder.getOrderDate().toString(),
                savedOrder.getStatus(),
                savedOrder.getTotalAmount(),
                savedOrder.getUserId(),
                orderItems
        );
    }

    // get User details
    public UserDetails getUserDetails(Long userId) {
        try {
        UserDetails userDetails = restTemplate.getForObject("http://user-service/api/users/" + userId, UserDetails.class);
        System.out.println("Retrieved user details: " + userDetails);
        return userDetails;
        }
        catch (Exception e){
            throw new RuntimeException("User service is down" + e.getMessage());
        }


    }


    }

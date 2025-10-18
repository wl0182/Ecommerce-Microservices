package com.wassimlagnaoui.ecommerce.order_service.Controller;


import com.wassimlagnaoui.ecommerce.order_service.DTO.*;
import com.wassimlagnaoui.ecommerce.order_service.Service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    // Implement order-related endpoints here
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }



    // create new Order
    @PostMapping("/{userId}")
    public ResponseEntity<OrderCreatedResponse> createOrder(@PathVariable("userId") Long userId,@RequestBody CreateOrderDTO createOrderDTO) {
        OrderCreatedResponse orderDTO = orderService.createOrder(userId,createOrderDTO);
        return ResponseEntity.ok(orderDTO);
    }

    // get order by id
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable("orderId") Long orderId) {
        OrderDTO orderDTO = orderService.getOrderById(orderId);
        return ResponseEntity.ok(orderDTO);
    }

    // get order by user id
    public ResponseEntity<List<OrderDTO>> getOrdersByUserId(@PathVariable("userId") Long userId) {
        List<OrderDTO> orderDTO = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orderDTO);
    }

    // cancel order
    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<OrderCancelled> cancelOrder(@PathVariable("orderId") Long orderId) {
        OrderCancelled response = orderService.cancelOrder(orderId);
        return ResponseEntity.ok(response);
    }


    // get order status
    @GetMapping("/status/{orderId}")
    public ResponseEntity<OrderStatusDTO> getOrderStatus(@PathVariable("orderId") Long orderId) {
        OrderStatusDTO status = orderService.getOrderStatus(orderId);
        return ResponseEntity.ok(status);
    }







}

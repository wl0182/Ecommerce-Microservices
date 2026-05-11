package com.wassimlagnaoui.ecommerce.order_service.Controller;


import com.wassimlagnaoui.ecommerce.order_service.DTO.*;
import com.wassimlagnaoui.ecommerce.order_service.Entities.OrderStatus;
import com.wassimlagnaoui.ecommerce.order_service.Service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    // Implement order-related endpoints here
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }



    // create new Order
    @PostMapping("/place-order")
    public ResponseEntity<OrderCreatedResponse> createOrder(@RequestParam("userId") Long userId,@Valid @RequestBody CreateOrderDTO createOrderDTO) {
        OrderCreatedResponse orderDTO = orderService.placeOrder(userId,createOrderDTO);
        return ResponseEntity.ok(orderDTO);
    }

    // get order by id
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable("orderId") Long orderId) {
        OrderDTO orderDTO = orderService.getOrderById(orderId);
        return ResponseEntity.ok(orderDTO);
    }

    // get order by user id
    @GetMapping("/user/{userId}")
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


    // Get Products
    @GetMapping("/product/{productId}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable("productId") Long productId) {
        ProductDTO productDTO = orderService.getProductByIdService(productId);
        return ResponseEntity.ok(productDTO);
    }


    // Update Order Status manually (for testing purposes)
    @PutMapping("/{orderId}/update-status")
    public ResponseEntity<OrderDTO> updateOrderStatus(@PathVariable("orderId") Long orderId,@Valid @RequestBody OrderStatusDTO status) {
        OrderDTO updatedStatus = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(updatedStatus);
    }


    // get all orders
    @GetMapping("/all")
    public ResponseEntity<Page<OrderDTO>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Page<OrderDTO> orders = orderService.getAllOrders(page, size);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/status")
    public ResponseEntity<List<OrderDTO>> getOrdersByStatus(@RequestParam("status") OrderStatus status) {
        List<OrderDTO> orders = orderService.findOrderByStatus(status);
        return ResponseEntity.ok(orders);
    }







}

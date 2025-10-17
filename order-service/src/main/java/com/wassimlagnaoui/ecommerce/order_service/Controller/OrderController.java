package com.wassimlagnaoui.ecommerce.order_service.Controller;


import com.wassimlagnaoui.ecommerce.order_service.Service.OrderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    // Implement order-related endpoints here
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }




}

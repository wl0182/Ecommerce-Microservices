package com.wassimlagnaoui.ecommerce.order_service.Controller;


import com.wassimlagnaoui.ecommerce.order_service.DTO.CreateOrderDTO;
import com.wassimlagnaoui.ecommerce.order_service.DTO.OrderDetails;
import com.wassimlagnaoui.ecommerce.order_service.DTO.UserDetails;
import com.wassimlagnaoui.ecommerce.order_service.Service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    // Implement order-related endpoints here
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // get Order by id
    @GetMapping("/{id}")
    ResponseEntity<OrderDetails> getOrderById(@PathVariable Long id){
        OrderDetails orderDetails = orderService.getOrderById(id);
        if(orderDetails != null){
            return ResponseEntity.ok(orderDetails);
        }else{
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping
    ResponseEntity<OrderDetails> createOrder(@RequestBody CreateOrderDTO createOrderDTO){
        OrderDetails orderDetails = orderService.createOrder(createOrderDTO);
        return ResponseEntity.ok(orderDetails);
    }

    @GetMapping("/user/{userId}")
    ResponseEntity<UserDetails> getUserDetails(@PathVariable Long userId){
      UserDetails userDetails = orderService.getUserDetails(userId);
        if(userDetails != null){
            return ResponseEntity.ok(userDetails);
        }else{
            return ResponseEntity.notFound().build();
        }

    }





}

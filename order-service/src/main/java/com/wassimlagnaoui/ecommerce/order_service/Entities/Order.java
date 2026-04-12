package com.wassimlagnaoui.ecommerce.order_service.Entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    private Long userId;


    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,orphanRemoval = true ,fetch = FetchType.LAZY)
    private List<OrderItem> orderItems= new ArrayList<>();

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(precision = 13, scale = 2)
    private BigDecimal totalAmount;

    private LocalDateTime lastUpdated;

    // add item helper method to maintain bidirectional relationship
    public void addOrderItem(OrderItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Order item cannot be null");
        }
        orderItems.add(item);
        item.setOrder(this);
    }

     // remove item helper method to maintain bidirectional relationship
    public void removeOrderItem(OrderItem item) {
        orderItems.remove(item);
        item.setOrder(null);
    }


}

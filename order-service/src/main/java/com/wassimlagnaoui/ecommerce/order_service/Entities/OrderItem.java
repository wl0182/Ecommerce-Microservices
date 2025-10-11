package com.wassimlagnaoui.ecommerce.order_service.Entities;


import jakarta.persistence.*;
import jdk.jfr.Enabled;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "order_items")
@Entity
public class OrderItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int quantity;
    private double price;

    private Long productId; // Storing productId instead of Product entity to keep bounded context simple

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;



}

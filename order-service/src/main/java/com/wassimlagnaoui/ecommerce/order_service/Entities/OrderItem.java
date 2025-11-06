package com.wassimlagnaoui.ecommerce.order_service.Entities;


import jakarta.persistence.*;
import jdk.jfr.Enabled;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "order_items")
@Entity
public class OrderItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int quantity;

    private BigDecimal price; // BigDecimal for monetary values because of precision

    private Long productId;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;



}

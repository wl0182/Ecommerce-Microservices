package com.wassimlagnaoui.ecommerce.product_service.Domain;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String name;
    private String description;
    private Double price;
    private String sku;

    private Integer stockQuantity;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;


    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InventoryTransaction> inventoryTransactions;




}

/*
 * Product → { id, name, description, price, sku, categoryId, stockQuantity }
 * Category → { id, name }
 * InventoryTransaction → { id, productId, type[ADD/REMOVE], quantity, timestamp }
 */


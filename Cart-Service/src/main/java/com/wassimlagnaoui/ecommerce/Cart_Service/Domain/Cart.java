package com.wassimlagnaoui.ecommerce.Cart_Service.Domain;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "carts")
public class Cart {

    @Id @GeneratedValue
    private Long id;
    private Long userId;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true ,fetch = FetchType.EAGER)
    private List<CartItem> cartItems;


}

// orphanRemoval = true means that if a CartItem is removed from the cartItems list, it will be deleted from the cart_items table in the database.

/*
 * Cart → { id, userId, List<CartItem> }
 * CartItem → { id, productId, quantity, price }
 */
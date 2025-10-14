package com.wassimlagnaoui.ecommerce.Cart_Service.Repository;


import com.wassimlagnaoui.ecommerce.Cart_Service.Domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // find cart items by cart id
    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.id = ?1")
    List<CartItem> findByCartId(Long cartId);


}

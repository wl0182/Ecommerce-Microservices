package com.wassimlagnaoui.ecommerce.Cart_Service.Repository;


import com.wassimlagnaoui.ecommerce.Cart_Service.Domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

}

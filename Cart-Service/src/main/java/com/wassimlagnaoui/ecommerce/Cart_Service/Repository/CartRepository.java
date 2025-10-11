package com.wassimlagnaoui.ecommerce.Cart_Service.Repository;

import com.wassimlagnaoui.ecommerce.Cart_Service.Domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
}

package com.wassimlagnaoui.ecommerce.Cart_Service.Repository;

import com.wassimlagnaoui.ecommerce.Cart_Service.Domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    @Query("SELECT c FROM Cart c WHERE c.userId = ?1")
    Optional<Cart> findByUserId(Long userId);
}

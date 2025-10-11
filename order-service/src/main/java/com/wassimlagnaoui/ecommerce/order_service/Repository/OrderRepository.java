package com.wassimlagnaoui.ecommerce.order_service.Repository;

import com.wassimlagnaoui.ecommerce.order_service.Entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o JOIN FETCH o.orderItems WHERE o.id = :id")
    List<Order> findOrderWithItemsById(Long id);

    List<Order> findByUserId(Long userId);

}

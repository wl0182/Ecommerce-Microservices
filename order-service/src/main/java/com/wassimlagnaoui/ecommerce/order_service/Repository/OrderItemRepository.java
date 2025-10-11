package com.wassimlagnaoui.ecommerce.order_service.Repository;

import com.wassimlagnaoui.ecommerce.order_service.Entities.Order;
import com.wassimlagnaoui.ecommerce.order_service.Entities.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId")
    List<OrderItem> findOrderItemsByOrderId(Long orderId);
}

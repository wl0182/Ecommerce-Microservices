package com.wassimlagnaoui.ecommerce.order_service.Repository;

import com.wassimlagnaoui.ecommerce.order_service.Entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o JOIN FETCH o.orderItems WHERE o.id = :id")
    List<Order> findOrderWithItemsById(Long id);

    List<Order> findByUserId(Long userId);


    // avoid N+1 problem when fetching orders with their items
    @Query("SELECT o FROM Order o JOIN FETCH o.orderItems WHERE o.userId = :userId")
    List<Order> findOrderByUserIdWithItems(Long userId);


    // fetch order with items by order id
    @Query("SELECT o FROM Order o JOIN FETCH o.orderItems WHERE o.id = :orderId")
    Optional<Order> findOrderWithItems(Long orderId);

    // find top 5 ordered Products
    // query: select oi.product_id, count(*) from order_db.orders o
    //join order_db.order_items oi on o.id = oi.order_id
    //group by 1 order by 2 desc limit 5;

    @Query(nativeQuery = true, value = "select oi.product_id, count(*) as order_count from orders o " +
            "join order_items oi on o.id = oi.order_id " +
            "group by oi.product_id " +
            "order by order_count desc " +
            "limit 5")
    List<Object[]> findTop5OrderedProducts();


}

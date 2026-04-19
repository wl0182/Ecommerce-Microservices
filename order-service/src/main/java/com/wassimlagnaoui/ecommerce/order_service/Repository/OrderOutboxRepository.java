package com.wassimlagnaoui.ecommerce.order_service.Repository;

import com.wassimlagnaoui.ecommerce.order_service.Entities.EventStatus;
import com.wassimlagnaoui.ecommerce.order_service.Entities.OrderOutbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface OrderOutboxRepository extends JpaRepository<OrderOutbox,UUID> {


    @Query("select o from OrderOutbox o where o.status in ('PENDING','FAILED') order by o.createdAt DESC limit 10 ")
    List<OrderOutbox> findUnprocessedEvents();
}

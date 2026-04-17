package com.wassimlagnaoui.ecommerce.product_service.Repository;

import com.wassimlagnaoui.ecommerce.product_service.Domain.ProductOutbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ProductOutboxRepository extends JpaRepository<ProductOutbox, UUID> {
    // find all events with status PENDING OR FAILED
    @Query("SELECT p FROM ProductOutbox p WHERE p.status = 'PENDING' OR p.status = 'FAILED' order by p.createdAt DESC limit 100")
    List<ProductOutbox> findUnprocessedEvents();
    
}

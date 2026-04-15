package com.wassimlagnaoui.ecommerce.user_service.Repository;

import com.wassimlagnaoui.ecommerce.user_service.Model.UserOutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface UserOutboxRepository extends JpaRepository<UserOutboxEvent, UUID> {

    @Query("SELECT e FROM UserOutboxEvent e WHERE e.status in ('PENDING','FAILED') ")
    List<UserOutboxEvent> findUnprocessedEvents();

}

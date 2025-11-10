package com.wassimlagnaoui.ecommerce.Notification_Service.Repository;

import com.wassimlagnaoui.ecommerce.Notification_Service.Domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification,Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM notifications n WHERE n.user_id = ?1")
    List<Notification> findByUserId(Long userId);
}

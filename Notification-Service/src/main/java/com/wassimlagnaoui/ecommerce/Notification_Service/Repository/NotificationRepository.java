package com.wassimlagnaoui.ecommerce.Notification_Service.Repository;

import com.wassimlagnaoui.ecommerce.Notification_Service.Domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification,Long> {

}

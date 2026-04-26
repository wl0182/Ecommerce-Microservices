package com.wassimlagnaoui.ecommerce.Payment_Service.Repository;

import com.wassimlagnaoui.ecommerce.Payment_Service.Domain.EventStatus;
import com.wassimlagnaoui.ecommerce.Payment_Service.Domain.PaymentOutbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PaymentOutboxRepository extends JpaRepository<PaymentOutbox, UUID> {

    // select all pending events
    List<PaymentOutbox> findByStatus(EventStatus status);
}

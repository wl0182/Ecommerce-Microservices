package com.wassimlagnaoui.ecommerce.Payment_Service.Repository;

import com.wassimlagnaoui.ecommerce.Payment_Service.Domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,Long> {

    @Query("SELECT p FROM Payment p WHERE p.orderId = ?1")
    List<Payment> findByOrderId(Long orderId);
}

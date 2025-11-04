package com.wassimlagnaoui.ecommerce.Payment_Service.Repository;

import com.wassimlagnaoui.ecommerce.Payment_Service.Domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,Long> {

}

package com.wassimlagnaoui.ecommerce.Payment_Service.Repository;

import com.wassimlagnaoui.ecommerce.Payment_Service.Domain.Refund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefundRepository extends JpaRepository<Refund,Long> {

}

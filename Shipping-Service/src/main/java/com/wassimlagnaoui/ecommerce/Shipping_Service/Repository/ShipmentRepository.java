package com.wassimlagnaoui.ecommerce.Shipping_Service.Repository;

import com.wassimlagnaoui.ecommerce.Shipping_Service.Domain.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment,Long> {



    // get Shipment by orderId
    @Query("SELECT s FROM Shipment s WHERE s.orderId = ?1")
    Optional<Shipment> findByOrderId(Long orderId);
}

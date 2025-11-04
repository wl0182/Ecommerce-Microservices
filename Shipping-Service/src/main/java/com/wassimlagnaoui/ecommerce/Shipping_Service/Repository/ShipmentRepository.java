package com.wassimlagnaoui.ecommerce.Shipping_Service.Repository;

import com.wassimlagnaoui.ecommerce.Shipping_Service.Domain.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment,Long> {
}

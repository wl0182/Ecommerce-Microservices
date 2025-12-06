package com.wassimlagnaoui.ecommerce.Shipping_Service.Controller;

import com.wassimlagnaoui.ecommerce.Shipping_Service.DTO.ShipRequest;
import com.wassimlagnaoui.ecommerce.Shipping_Service.DTO.ShipmentDTO;
import com.wassimlagnaoui.ecommerce.Shipping_Service.Service.ShipmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shipments")
public class ShipmentController {

    private final ShipmentService shipmentService;

    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShipmentDTO> getShipmentById(@PathVariable Long id) {
        ShipmentDTO shipmentDTO = shipmentService.getShipmentById(id);
        return ResponseEntity.ok(shipmentDTO);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ShipmentDTO> getShipmentByOrderId(@PathVariable Long orderId) {
        ShipmentDTO shipmentDTO = shipmentService.getShipmentByOrderId(orderId);
        return ResponseEntity.ok(shipmentDTO);
    }

    // ship order
    @PostMapping("/ship/order/{orderId}")
    public ResponseEntity<ShipmentDTO> shipOrder(@PathVariable Long orderId, @RequestBody ShipRequest shipRequest) {
        ShipmentDTO shipmentDTO = shipmentService.shipOrder(orderId, shipRequest);
        return ResponseEntity.ok(shipmentDTO);
    }

}

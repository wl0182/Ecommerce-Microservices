package com.wassimlagnaoui.ecommerce.order_service.Controller;

import com.wassimlagnaoui.ecommerce.order_service.DTO.TopProductsResponse;
import com.wassimlagnaoui.ecommerce.order_service.Service.StatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/stats")
public class StatsController {
    private final StatService statService;
    public StatsController(StatService statService) {
        this.statService = statService;
    }

    // get top 5 best-selling products
    @GetMapping("/top-products")
    public ResponseEntity<List<TopProductsResponse>> getTopSellingProducts() {
        List<TopProductsResponse> topProducts = statService.getTopSellingProducts();
        return ResponseEntity.ok(topProducts);
    }
}

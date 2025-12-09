package com.wassimlagnaoui.ecommerce.Cart_Service.Service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CartCleanupScheduler {

    private final CartService cartService;
    public CartCleanupScheduler(CartService cartService) {
        this.cartService = cartService;
    }

    @Scheduled(fixedRate = 3600000)
    public void cleanupOldCarts() {
        cartService.cleanupOldCarts();
    }
}

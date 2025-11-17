package com.wassimlagnaoui.ecommerce.Cart_Service.Controller;

import com.wassimlagnaoui.ecommerce.Cart_Service.DTO.*;
import com.wassimlagnaoui.ecommerce.Cart_Service.Service.CartService;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // add item to the cart

    @PostMapping("/{userId}/items")
    public ResponseEntity<CartItemDTO> addItemToCart(@PathVariable Long userId, @RequestBody AddItemRequest addItemRequest) {
        CartItemDTO response = cartService.addItemToCart(userId, addItemRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getCartByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getCartByUserId(userId));
    }

    // remove item from cart
    @DeleteMapping("/{userId}/items/{productId}")
    public ResponseEntity<ResponseMessage> removeItemFromCart(@PathVariable Long userId, @PathVariable Long productId) {
        ResponseMessage responseMessage = cartService.removeItemFromCart(userId, productId);
        return ResponseEntity.ok(responseMessage);
    }

    // clear Cart
    @DeleteMapping("/{userId}")
    public ResponseEntity<ResponseMessage> clearCart(@PathVariable Long userId) {
        ResponseMessage responseMessage = cartService.clearCart(userId);
        return ResponseEntity.ok(responseMessage);
    }

    // checkout
    @PostMapping("/{userId}/checkout")
    public ResponseEntity<CheckoutResponse> checkout(@PathVariable Long userId, @RequestBody CheckoutRequest checkoutRequest) {
        CheckoutResponse responseMessage = cartService.checkoutCart(userId, checkoutRequest);
        return ResponseEntity.ok(responseMessage);

    }


}

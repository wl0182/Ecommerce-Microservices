package com.wassimlagnaoui.ecommerce.Cart_Service.Service;

import com.wassimlagnaoui.common_events.Events.CartService.CartClearedEvent;
import com.wassimlagnaoui.ecommerce.Cart_Service.DTO.*;
import com.wassimlagnaoui.ecommerce.Cart_Service.DTO.RestDTOs.*;
import com.wassimlagnaoui.ecommerce.Cart_Service.Domain.Cart;
import com.wassimlagnaoui.ecommerce.Cart_Service.Domain.CartItem;
import com.wassimlagnaoui.ecommerce.Cart_Service.Exception.*;
import com.wassimlagnaoui.ecommerce.Cart_Service.Repository.CartItemRepository;
import com.wassimlagnaoui.ecommerce.Cart_Service.Repository.CartRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;

    private final ProductRestClient productRestClient;
    private final OrderRestClient orderRestClient;

    @Autowired
    private final RestTemplate restTemplate;

    // services
    @Value("${services.order-service.url}")
    private String orderServiceUrl ;

    @Value("${services.product-service.url}")
    private String productServiceUrl;

    @Autowired
    private final KafkaEventPublisher kafkaEventPublisher;



    public CartService(CartItemRepository cartItemRepository, CartRepository cartRepository, ProductRestClient productRestClient, OrderRestClient orderRestClient, RestTemplate restTemplate, KafkaEventPublisher kafkaEventPublisher) {
        this.cartItemRepository = cartItemRepository;
        this.cartRepository = cartRepository;
        this.productRestClient = productRestClient;
        this.orderRestClient = orderRestClient;
        this.restTemplate = restTemplate;
        this.kafkaEventPublisher = kafkaEventPublisher;
    }

    // Get cart by user ID
    @Transactional(readOnly = true)
    public CartDTO getCartByUserId(Long userId) {
        // retrieve cart by user id
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> new CartNotFoundException("Cart not found for user ID: " + userId));

        // retrieve cart items by cart id
        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
        if (cartItems.isEmpty()) {
            return CartDTO.builder()
                    .userId(userId)
                    .items(List.of())
                    .totalAmount(BigDecimal.ZERO)
                    .build();
        }

        // Get the products by cart Items
        List<Long> productIds = cartItems.stream().map(CartItem::getProductId).distinct().toList();
        List<ProductDTO> products = productRestClient.getProductsByIds(productIds);
        Map<Long,ProductDTO> productMap = new HashMap<>();

        for (ProductDTO productDTO: products){
            productMap.put(productDTO.getId(),productDTO);
        }

        // Convert CartItems to CartItemDTO adding productName
        List<CartItemDTO> cartItemDTOList = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem: cartItems){
            CartItemDTO cartItemDTO = new CartItemDTO();
            cartItemDTO.setId(cartItem.getId());
            cartItemDTO.setCartId(cart.getId());

            // product related Data
            cartItemDTO.setProductId(cartItem.getProductId());
            ProductDTO productDTO = productMap.get(cartItem.getProductId());
            String productName = productDTO == null ? "Unknow" : productDTO.getName();
            cartItemDTO.setProductName(productName);

            //Quantity and price
            cartItemDTO.setQuantity(cartItem.getQuantity());
            cartItemDTO.setPrice(cartItem.getPrice());

            cartItemDTOList.add(cartItemDTO);

            totalAmount = totalAmount.add(cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));

        }

        return CartDTO.builder().items(cartItemDTOList).userId(userId).totalAmount(totalAmount).build();

    }// { userId, items:[{ productId, productName, quantity, price }], totalAmount }

    // Add Item to Cart

    public CartItemDTO addItemToCart(Long userId,AddItemRequest addItemRequest){
        // 1. Validate Input
        if (addItemRequest.getQuantity()<=0){
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        // 2. Retrieve Product and Inventory from Product Service
        ProductDTO productDTO = productRestClient.getProductById(addItemRequest.getProductId());
        InventoryDTO inventoryDTO = productRestClient.getProductInventoryById(addItemRequest.getProductId());

        // 3. Validate if the Product or Inventory are null
        if (productDTO == null|| inventoryDTO == null) {
            throw new ProductNotFoundException("Product not found with id: " + addItemRequest.getProductId());
        }
        // 4. Check if Cart Exist for user, otherwise create one
        Cart cart = cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUserId(userId);
            newCart.setCreatedAt(LocalDateTime.now());
            newCart.setUpdatedAt(LocalDateTime.now());
            return cartRepository.save(newCart);
        });
        // 5. check if CartItem exist for same product and user id, otherwise create one
        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), addItemRequest.getProductId())
                    .orElseGet(() -> {
                        CartItem newCartItem = new CartItem();
                        newCartItem.setCart(cart);
                        cart.getCartItems().add(newCartItem); // add cart item to cart's cart items list to establish the relationship and ensure it is saved when we save the cart
                        newCartItem.setProductId(addItemRequest.getProductId());
                        newCartItem.setQuantity(0); // set initial quantity to 0, we will update it later after validating the total quantity against inventory stock
                        return newCartItem;
                    });
        // 6. Set the Quantity and Validate Quantity again against inventory stock
        int newQuantity = cartItem.getQuantity() + addItemRequest.getQuantity();
        if (inventoryDTO.getStockInventory()<newQuantity){
            throw new QuantityUnavailable("Requested quantity of product with ID: " + addItemRequest.getProductId() + " is not available in stock. Available stock: " + inventoryDTO.getStockInventory());
        }
        BigDecimal totalPrice = productDTO.getPrice().multiply(BigDecimal.valueOf(newQuantity));
        // 7. Update CartItem with new quantity and price
        cartItem.setQuantity(newQuantity);
        cartItem.setPrice(totalPrice);
        // 8. Save  Cart and CartItem to Db
        cart.setUpdatedAt(LocalDateTime.now());
        CartItem savedCartItem = cartItemRepository.save(cartItem);
        cartRepository.save(cart);
       // 9. Map CartItem to CartItemDTO and return it
        return mapToCartItemDTO(savedCartItem, productDTO);
    } // { id, cartId, productId, productName, quantity, price }

    private CartItemDTO mapToCartItemDTO(CartItem cartItem, ProductDTO product) {
        CartItemDTO cartItemDTO = new CartItemDTO();
        cartItemDTO.setProductId(product.getId());
        cartItemDTO.setProductName(product.getName());
        cartItemDTO.setQuantity(cartItem.getQuantity());
        cartItemDTO.setPrice(product.getPrice());
        cartItemDTO.setCartId(cartItem.getCart().getId());
        cartItemDTO.setId(cartItem.getId());
        return cartItemDTO;
    }

    // remove item from cart
    @Transactional
    public ResponseMessage removeItemFromCart(Long userId, Long productId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user ID: " + userId));
        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found in cart with ID: " + productId));

        log.info("Retrieved cart item with ID: " + cartItem.getId() + " and Product ID: " + productId + " for removal.");

        // delete cart item fron cart and then cart_items table
        cartItem.setCart(null);
        cart.getCartItems().remove(cartItem);
        cart.setUpdatedAt(LocalDateTime.now());

       // cartRepository.save(cart); // save cart to trigger orphanRemoval and delete cart item from cart_items table

        // cartItemRepository.delete(cartItem) did not work as expected here for unknown reasons
        log.info("Deleted cart item with ID: " + cartItem.getId() + " and Product ID: " + productId + " from cart.");

        return new ResponseMessage("Product with ID: " + productId + " removed from cart.");
    }




    // clear cart
    @Transactional
    public ResponseMessage clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user ID: " + userId));

        cart.getCartItems().clear();

        log.info("Cleared cart for user ID: " + userId);
        cart.setUpdatedAt(LocalDateTime.now());

        return new ResponseMessage("Cart cleared for user ID: " + userId);
    }


    // Checkout cart
    public CheckoutResponse checkoutCart(Long userId, CheckoutRequest checkoutRequest) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user ID: " + userId));
        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());

        if (cartItems.isEmpty()) {
            throw new CartNotFoundException("Cart is empty for user ID: " + userId);
        }

        List<OrderItemRequest> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            OrderItemRequest orderItem = new OrderItemRequest();
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItems.add(orderItem);
        }

        CreateOrderDTO createOrderDTO = new CreateOrderDTO();
        createOrderDTO.setUserId(userId);
        createOrderDTO.setItems(orderItems);
       // Input Validation for checkoutRequest fields,
        if (checkoutRequest == null || checkoutRequest.getPaymentMethod() == null ) {
            createOrderDTO.setPaymentMethod("UNKNOWN");

        }
        if (checkoutRequest == null || checkoutRequest.getAddressId() == null ) {
            createOrderDTO.setAddressId(-1L);
        }

        createOrderDTO.setPaymentMethod(checkoutRequest.getPaymentMethod().name());
        createOrderDTO.setAddressId(checkoutRequest.getAddressId());



        // Call order service to create order
        OrderCreatedResponse orderCreatedResponse = orderRestClient.placeOrder(userId, createOrderDTO);
        // If order creation failed, throw exception
        if (orderCreatedResponse == null || "FAILED".equals(orderCreatedResponse.getStatus())) {
            throw new OrderServiceDownException("Order Service is currently unavailable. Please try again later.");
        }
        // Clear cart after successful order creation
        clearCart(userId);
        // Return checkout response
        CheckoutResponse checkoutResponse = new CheckoutResponse();
        checkoutResponse.setOrderId(orderCreatedResponse.getId());
        checkoutResponse.setStatus(orderCreatedResponse.getStatus());
        checkoutResponse.setTotalAmount(orderCreatedResponse.getTotalAmount());
        checkoutResponse.setCreatedAt(orderCreatedResponse.getCreatedAt());
        checkoutResponse.setUserId(userId);

        return checkoutResponse;

    }



    public void cleanupOldCarts() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(1);
        List<Cart> oldCarts = cartRepository.findByUpdatedAtBefore(cutoffTime);
        for (Cart cart : oldCarts) {
            log.info("Cleaning up cart with ID: " + cart.getId() + " for user ID: " + cart.getUserId());
            cart.getCartItems().clear();
            cart.setUpdatedAt(LocalDateTime.now());

        }

    }




}

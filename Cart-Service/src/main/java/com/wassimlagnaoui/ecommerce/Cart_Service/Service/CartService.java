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

    @Transactional
    public CartItemDTO addItemToCart(Long userId,AddItemRequest addItemRequest){
       // validate Input
        if (addItemRequest == null || addItemRequest.getProductId() == null || addItemRequest.getQuantity() == null || addItemRequest.getQuantity() <= 0) {
            throw new IllegalArgumentException("Invalid input: productId and quantity must be provided and quantity must be greater than 0");
        }
        // retrieve product details
        ProductDetails productDetails = getProductDetailsById(addItemRequest.getProductId());
        if (productDetails.getId()==null) {
            throw new ProductNotFoundException("Product not found with ID: " + addItemRequest.getProductId());
        }
        if (productDetails.getId().equals(-1L)){
            throw new ProductServiceUnavailble("Product Service is currently unavailable, failed to retrieve product details for product ID: " + addItemRequest.getProductId());
        }

        if (productDetails.getStockQuantity() < addItemRequest.getQuantity()) {
            throw new QuantityUnavailable("Insufficient stock for product ID: " + addItemRequest.getProductId() + ". Available stock: " + productDetails.getStockQuantity());
        }


        // retrieve Cart by User id or create new Cart
        Cart cart = cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUserId(userId);
            newCart.setCreatedAt(LocalDateTime.now());
            newCart.setUpdatedAt(LocalDateTime.now());
            return cartRepository.save(newCart);
        });

        // Retrieve existing CartItem and add or create a new one CartItem
        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), addItemRequest.getProductId())
                .orElseGet(() -> {
                    CartItem newCartItem = new CartItem();
                    newCartItem.setProductId(addItemRequest.getProductId());
                    newCartItem.setQuantity(0);
                    newCartItem.setPrice(productDetails.getPrice());
                    cart.addCartItem(newCartItem); // add cart item to cart and set cart reference in cart item
                    return newCartItem;
                });

        cartItem.setQuantity(cartItem.getQuantity() + addItemRequest.getQuantity());


        // Save Cart
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart); // no need to save cartItem separately because of cascade and orphanRemoval


        // return CartItemDTO with product details and quantity
        return  CartItemDTO.builder()
                .cartId(cart.getId())
                .productId(cartItem.getProductId())
                .productName(productDetails.getName())
                .quantity(cartItem.getQuantity())
                .price(cartItem.getPrice())
                .build();
    }

    private CartItemDTO mapToCartItemDTO(CartItem cartItem, ProductDTO product) {
        CartItemDTO cartItemDTO = new CartItemDTO();
        cartItemDTO.setProductId(product.getId());
        cartItemDTO.setProductName(product.getName());
        cartItemDTO.setQuantity(cartItem.getQuantity());
        cartItemDTO.setPrice(product.getPrice());
        cartItemDTO.setCartId(cartItem.getCart().getId());
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
    @Transactional
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
            orderItem.setPrice(cartItem.getPrice());
            orderItems.add(orderItem);
        }

        CreateOrderDTO createOrderDTO = new CreateOrderDTO();
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
        if (orderCreatedResponse.getId() == null) {
            throw new InvalidAddress("Invalid address ID: " + checkoutRequest.getAddressId() + " for user ID: " + userId);
        }
        if (orderCreatedResponse.getId().equals(-1L)) {
            log.warn("Order Service circuit breaker is open, failed to place order for user ID: " + userId);
            throw new OrderServiceDownException("Order Service failed , Circuit breaker is open, failed to place order for user ID: " + userId);
        }
        // Clear cart after successful order creation
        cart.getCartItems().clear();
        log.info("Cleared cart for user ID: " + userId + " after successful order creation with Order ID: " + orderCreatedResponse.getId());
        cart.setUpdatedAt(LocalDateTime.now());
        // Return checkout response
        CheckoutResponse checkoutResponse = new CheckoutResponse();
        checkoutResponse.setOrderId(orderCreatedResponse.getId());
        checkoutResponse.setStatus(orderCreatedResponse.getStatus());
        checkoutResponse.setTotalAmount(orderCreatedResponse.getTotalAmount());
        checkoutResponse.setCreatedAt(orderCreatedResponse.getCreatedAt());
        checkoutResponse.setUserId(userId);

        return checkoutResponse;

    }



    @Transactional
    public void cleanupOldCarts() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(1);
        List<Cart> oldCarts = cartRepository.findByUpdatedAtBefore(cutoffTime);
        for (Cart cart : oldCarts) {
            log.info("Cleaning up cart with ID: " + cart.getId() + " for user ID: " + cart.getUserId());
            cart.getCartItems().clear();
            cart.setUpdatedAt(LocalDateTime.now());

        }

    }


    public ProductDetails getProductDetailsById(Long productId){
        ProductDetails productDetails = productRestClient.getProductDetailsById(productId);
        log.error("Product Service returned response for product details with id: " + productId + " is: " + productDetails);
        if (productDetails.getId()==null) {
            log.warn("Product Service returned null response for product details with id: " + productId);
            throw new ProductNotFoundException("Product details not found for product ID: " + productId);
        }
        return productDetails;
    }


}

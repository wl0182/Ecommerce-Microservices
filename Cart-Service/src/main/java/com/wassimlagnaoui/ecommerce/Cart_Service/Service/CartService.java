package com.wassimlagnaoui.ecommerce.Cart_Service.Service;

import com.wassimlagnaoui.common_events.Events.CartService.CartClearedEvent;
import com.wassimlagnaoui.ecommerce.Cart_Service.DTO.*;
import com.wassimlagnaoui.ecommerce.Cart_Service.Domain.Cart;
import com.wassimlagnaoui.ecommerce.Cart_Service.Domain.CartItem;
import com.wassimlagnaoui.ecommerce.Cart_Service.Exception.CartNotFoundException;
import com.wassimlagnaoui.ecommerce.Cart_Service.Exception.OrderServiceDownException;
import com.wassimlagnaoui.ecommerce.Cart_Service.Exception.ProductNotFoundException;
import com.wassimlagnaoui.ecommerce.Cart_Service.Repository.CartItemRepository;
import com.wassimlagnaoui.ecommerce.Cart_Service.Repository.CartRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.ws.rs.DELETE;
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

    @Autowired
    private RestTemplate restTemplate;

    // services
    @Value("${services.order-service.url}")
    private String orderServiceUrl ;

    @Value("${services.product-service.url}")
    private String productServiceUrl;

    @Autowired
    private KafkaEventPublisher kafkaEventPublisher;



    public CartService(CartItemRepository cartItemRepository, CartRepository cartRepository) {
        this.cartItemRepository = cartItemRepository;
        this.cartRepository = cartRepository;
    }

    // Get cart by user ID
    @Transactional(readOnly = true)
    public CartDTO getCartByUserId(Long userId) {
        CartDTO cartResponse = new CartDTO();
        Optional<Cart> cartOptional = cartRepository.findByUserId(userId);
        if (cartOptional.isEmpty()) {
            throw new CartNotFoundException("Cart not found for user ID: " + userId);
        }
        Cart cart = cartOptional.get();


        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());

        List<Long> productIds = cartItems.stream().map(CartItem::getProductId).toList(); // extract product IDs from cart items
        List<ProductDTO> products = getProductsByIds(productIds);

        // map products by ID for easy lookup
        HashMap<Long,ProductDTO> productMap = mapProductsById(products);



        List<CartItemDTO> cartItemDTOS = new ArrayList<>();


        for (CartItem cartItem : cartItems) {
            ProductDTO product = productMap.get(cartItem.getProductId());
            if (product != null) {
                CartItemDTO cartItemDTO = new CartItemDTO();
                cartItemDTO.setCartId(cart.getId());
                cartItemDTO.setId(cartItem.getId());
                cartItemDTO.setProductId(product.getId());
                cartItemDTO.setProductName(product.getName());
                cartItemDTO.setQuantity(cartItem.getQuantity());
                cartItemDTO.setPrice(product.getPrice());
                cartItemDTOS.add(cartItemDTO);
            }
        }

        cartResponse.setUserId(userId);
        cartResponse.setItems(cartItemDTOS);
        double totalAmount = cartItemDTOS.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
        cartResponse.setTotalAmount(totalAmount);


        return cartResponse; // { userId, items:[{ productId, productName, quantity, price }], totalAmount }
    }


    // bulk getProducts by IDs
    public List<ProductDTO> getProductsByIds(List<Long> productIds) {
        ResponseEntity<ProductDTO[]> response = restTemplate.postForEntity("http://PRODUCT-SERVICE/products/bulk", productIds, ProductDTO[].class);
        return List.of(response.getBody());
    }
    // get Product by ID
    public ProductDTO getProductById(Long productId) {
        String url = productServiceUrl + "/products/" + productId;

        ResponseEntity<ProductDTO> response = restTemplate.getForEntity(url, ProductDTO.class);

        System.out.println(response.getStatusCode());
        System.out.println("Response from Product service is"+response.getBody().toString());

        return response.getBody();
    }

    // Transform ProductDTO list to Map<Long, ProductDTO>
    private HashMap<Long,ProductDTO> mapProductsById(List<ProductDTO> products) {
        HashMap<Long,ProductDTO> productMap = new HashMap<>();
        for (ProductDTO product : products) {
            productMap.put(product.getId(), product);
        }
        return productMap;
    }


    // Add Item to Cart
    @Transactional
    public CartItemDTO addItemToCart(Long userId,AddItemRequest addItemRequest){
        Long productId = addItemRequest.getProductId();
        Integer quantity = addItemRequest.getQuantity();

        // Check if product exists in Product Service
        ProductDTO product;

        try {
             product = getProductById(productId);
             log.info("Product retrieved: " + product.getName()+" with ID: " + product.getId() +" from Product Service.");
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new ProductNotFoundException("cannot add product with ID: " + productId + " to cart. Product service is unavailable or product does not exist.");
        }

        // check if cart exists for user
        Cart cart = cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUserId(userId);
            return cartRepository.save(newCart);
        });
        // check if product already exists in cart
        Optional<CartItem> existingCartItemOpt = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId);
        if (existingCartItemOpt.isPresent()) {
            CartItem existingCartItem = existingCartItemOpt.get();
            existingCartItem.setQuantity(existingCartItem.getQuantity() + quantity);
            CartItem updatedCartItem = cartItemRepository.save(existingCartItem);
            return mapToCartItemDTO(updatedCartItem, product);
        } else {
            CartItem newCartItem = new CartItem();
            newCartItem.setCart(cart);
            newCartItem.setProductId(productId);
            newCartItem.setQuantity(quantity);
            newCartItem.setPrice(product.getPrice());
            CartItem savedCartItem = cartItemRepository.save(newCartItem);
            return mapToCartItemDTO(savedCartItem, product);    }


    }

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
        cart.getCartItems().remove(cartItem);


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
        cartRepository.delete(cart);

        CartClearedEvent cartClearedEvent = CartClearedEvent.builder()
                .userId(userId)
                .clearedAt(Instant.now())
                .build();
        kafkaEventPublisher.publishCartClearedEvent(cartClearedEvent);


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
        if (checkoutRequest == null || checkoutRequest.getPaymentMethod() == null) {
            createOrderDTO.setPaymentMethod("UNKNOWN");
        } else {
            createOrderDTO.setPaymentMethod(checkoutRequest.getPaymentMethod().name());
        }
        createOrderDTO.setAddressId(checkoutRequest.getAddressId());

        // Call order service to create order
        OrderCreatedResponse orderCreatedResponse = createOrder(createOrderDTO);
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

    // call create order API in order-service
    @CircuitBreaker(name = "orderServiceCircuitBreaker", fallbackMethod = "createOrderFallback")
    public OrderCreatedResponse createOrder(CreateOrderDTO createOrderDTO) {
        ResponseEntity<OrderCreatedResponse> response = restTemplate.postForEntity(orderServiceUrl + "/api/orders/place-order", createOrderDTO, OrderCreatedResponse.class);

        log.info("Order Service response status: " + response.getStatusCode());
        log.info("Order Service response body: " + response.getBody());

        return response.getBody();
    }

    // Fallback method for createOrder
    public OrderCreatedResponse createOrderFallback(CreateOrderDTO createOrderDTO, Throwable throwable) {
        return OrderCreatedResponse.builder()
                .id(-1L)
                .userId(createOrderDTO.getUserId())
                .items(new ArrayList<>())
                .totalAmount(0.0)
                .status("FAILED")
                .createdAt(LocalDateTime.now().toString())
                .build();
    }







}

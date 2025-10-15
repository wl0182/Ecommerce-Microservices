package com.wassimlagnaoui.ecommerce.Cart_Service.Service;

import com.wassimlagnaoui.ecommerce.Cart_Service.DTO.*;
import com.wassimlagnaoui.ecommerce.Cart_Service.Domain.Cart;
import com.wassimlagnaoui.ecommerce.Cart_Service.Domain.CartItem;
import com.wassimlagnaoui.ecommerce.Cart_Service.Exception.CartNotFoundException;
import com.wassimlagnaoui.ecommerce.Cart_Service.Exception.ProductNotFoundException;
import com.wassimlagnaoui.ecommerce.Cart_Service.Repository.CartItemRepository;
import com.wassimlagnaoui.ecommerce.Cart_Service.Repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;

    @Autowired
    private RestTemplate restTemplate;



    public CartService(CartItemRepository cartItemRepository, CartRepository cartRepository) {
        this.cartItemRepository = cartItemRepository;
        this.cartRepository = cartRepository;
    }

    // Get cart by user ID
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
        ResponseEntity<ProductDTO> response = restTemplate.getForEntity("http://PRODUCT-SERVICE/products/" + productId, ProductDTO.class);
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
    public CartItemDTO addItemToCart(AddItemRequest addItemRequest){
        Long userId = addItemRequest.getUserId();
        Long productId = addItemRequest.getProductId();
        Integer quantity = addItemRequest.getQuantity();

        // Check if product exists in Product Service
        ProductDTO product;
        try {
            product = getProductById(productId);
        } catch (Exception e) {
            throw new ProductNotFoundException("Product not found with ID: " + productId);
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
    public ResponseMessage removeItemFromCart(Long userId, Long productId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user ID: " + userId));
        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found in cart with ID: " + productId));
        cartItemRepository.delete(cartItem);
        return new ResponseMessage("Product with ID: " + productId + " removed from cart.");
    }

    // clear cart
    public ResponseMessage clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user ID: " + userId));
        cartItemRepository.deleteByCartId(cart.getId());
        cartRepository.delete(cart);
        return new ResponseMessage("Cart cleared for user ID: " + userId);
    }


}

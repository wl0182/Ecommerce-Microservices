package com.wassimlagnaoui.ecommerce.Cart_Service.Service;

import com.wassimlagnaoui.ecommerce.Cart_Service.DTO.CartDTO;
import com.wassimlagnaoui.ecommerce.Cart_Service.DTO.CartItemDTO;
import com.wassimlagnaoui.ecommerce.Cart_Service.DTO.ProductDTO;
import com.wassimlagnaoui.ecommerce.Cart_Service.Domain.Cart;
import com.wassimlagnaoui.ecommerce.Cart_Service.Domain.CartItem;
import com.wassimlagnaoui.ecommerce.Cart_Service.Exception.CartNotFoundException;
import com.wassimlagnaoui.ecommerce.Cart_Service.Repository.CartItemRepository;
import com.wassimlagnaoui.ecommerce.Cart_Service.Repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    // Transform ProductDTO list to Map<Long, ProductDTO>
    private HashMap<Long,ProductDTO> mapProductsById(List<ProductDTO> products) {
        HashMap<Long,ProductDTO> productMap = new HashMap<>();
        for (ProductDTO product : products) {
            productMap.put(product.getId(), product);
        }
        return productMap;
    }

}

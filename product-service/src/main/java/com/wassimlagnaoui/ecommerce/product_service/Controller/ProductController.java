package com.wassimlagnaoui.ecommerce.product_service.Controller;


import com.wassimlagnaoui.ecommerce.product_service.DTO.CreateProductDTO;
import com.wassimlagnaoui.ecommerce.product_service.DTO.ProductDTO;
import com.wassimlagnaoui.ecommerce.product_service.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // Define endpoints here (e.g., getAllProducts, getProductById, createProduct)
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        ProductDTO product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody CreateProductDTO createProductDTO){
        ProductDTO productDTO = productService.createProduct(createProductDTO);
        return ResponseEntity.ok(productDTO);
    }


    @PutMapping("/{id}/price")
    public ResponseEntity<ProductDTO> updateProductPrice(@PathVariable Long id, @RequestParam Double newPrice) {
        ProductDTO updatedProduct = productService.updateProductPrice(id, newPrice);
        return ResponseEntity.ok(updatedProduct);
    }

    // increment stock
    @PostMapping("/{id}/increment-stock")
    public ResponseEntity<String> incrementStock(@PathVariable Long productId, @RequestParam Integer quantity) {
        String updatedProduct = productService.incrementInventory(productId, quantity);
        return ResponseEntity.ok(updatedProduct);
    }

    // decrement stock
    @PostMapping("/{id}/decrement-stock")
    public ResponseEntity<String> decrementStock(@PathVariable Long productId, @RequestParam Integer quantity) {
        String updatedProduct = productService.decrementInventory(productId, quantity);
        return ResponseEntity.ok(updatedProduct);
    }













}

package com.wassimlagnaoui.ecommerce.product_service.Controller;


import com.wassimlagnaoui.ecommerce.product_service.DTO.*;
import com.wassimlagnaoui.ecommerce.product_service.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
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
    @PutMapping("/{id}/increment-stock")
    public ResponseEntity<TransactionDTO> incrementStock(@PathVariable("id") Long id,@RequestBody UpdateProductInventoryDTO updateProductInventoryDTO) {
        TransactionDTO updatedProduct = productService.incrementInventory(id,updateProductInventoryDTO);
        return ResponseEntity.ok(updatedProduct);
    }

    // decrement stock
    @PutMapping("/{id}/decrement-stock")
    public ResponseEntity<TransactionDTO> decrementStock(@PathVariable("id") Long id,@RequestBody UpdateProductInventoryDTO updateProductInventoryDTO) {
        TransactionDTO updatedProduct = productService.decrementInventory(id,updateProductInventoryDTO);
        return ResponseEntity.ok(updatedProduct);
    }

    // Get Inventory by product id
    @GetMapping("/{id}/inventory")
    public ResponseEntity<InventoryDTO> getInventoryByProductId(@PathVariable Long id){
        InventoryDTO inventoryDTO = productService.getInventoryByProductId(id);
        return ResponseEntity.ok(inventoryDTO);

    }








    // Kafka test endpoint
    @PostMapping("/kafka-test")
    public ResponseEntity<String> sendTestMessageToKafka() {
        String response = productService.sendTestMessage();
        return ResponseEntity.ok(response);
    }


    // bulk get products by ids
    @PostMapping("/bulk")
    public ResponseEntity<List<ProductDTO>> getProductsByIds(@RequestBody List<Long> productIds) {
        List<ProductDTO> products = productService.getProductsByIds(productIds);
        return ResponseEntity.ok(products);
    }













}

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
    @PostMapping("/{id}/increment-stock")
    public ResponseEntity<TransactionDTO> incrementStock(@RequestBody UpdateProductInventoryDTO updateProductInventoryDTO) {
        TransactionDTO updatedProduct = productService.incrementInventory(updateProductInventoryDTO);
        return ResponseEntity.ok(updatedProduct);
    }

    // decrement stock
    @PostMapping("/{id}/decrement-stock")
    public ResponseEntity<TransactionDTO> decrementStock(@RequestBody UpdateProductInventoryDTO updateProductInventoryDTO) {
        TransactionDTO updatedProduct = productService.decrementInventory(updateProductInventoryDTO);
        return ResponseEntity.ok(updatedProduct);
    }


    // get All Categories


    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        List<CategoryDTO> categories = productService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    // create Category
    @PostMapping("/categories")
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CreateCategoryDTO createCategoryDTO) {
        CategoryDTO categoryDTO = productService.createCategory(createCategoryDTO);
        return ResponseEntity.ok(categoryDTO);
    }

    // Get Inventory by product id
    @GetMapping("/{id}/inventory")
    public ResponseEntity<InventoryDTO> getInventoryByProductId(@PathVariable Long id){
        InventoryDTO inventoryDTO = productService.getInventoryByProductId(id);
        return ResponseEntity.ok(inventoryDTO);

    }

    // update Category
    @PutMapping("/categories/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id, @RequestBody CreateCategoryDTO createCategoryDTO) {
        CategoryDTO categoryDTO = productService.updateCategory(id, createCategoryDTO);
        return ResponseEntity.ok(categoryDTO);
    }


    // Kafka test endpoint
    @PostMapping("/kafka-test")
    public ResponseEntity<String> sendTestMessageToKafka() {
        String response = productService.sendTestMessage();
        return ResponseEntity.ok(response);
    }





}

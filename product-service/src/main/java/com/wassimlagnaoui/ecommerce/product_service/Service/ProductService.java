package com.wassimlagnaoui.ecommerce.product_service.Service;

import com.wassimlagnaoui.ecommerce.product_service.DTO.*;
import com.wassimlagnaoui.ecommerce.product_service.Domain.Category;
import com.wassimlagnaoui.ecommerce.product_service.Domain.InventoryTransaction;
import com.wassimlagnaoui.ecommerce.product_service.Domain.Product;
import com.wassimlagnaoui.ecommerce.product_service.Domain.TransactionType;
import com.wassimlagnaoui.ecommerce.product_service.Exception.InsufficientStockException;
import com.wassimlagnaoui.ecommerce.product_service.Exception.ProductNotFoundException;
import com.wassimlagnaoui.ecommerce.product_service.Repository.CategoryRepository;
import com.wassimlagnaoui.ecommerce.product_service.Repository.InventoryTransactionRepository;
import com.wassimlagnaoui.ecommerce.product_service.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;

    // add KafkaTemplate
    @Autowired
    private  KafkaTemplate<String, String> kafkaTemplate;


    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, InventoryTransactionRepository inventoryTransactionRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.inventoryTransactionRepository = inventoryTransactionRepository;
    }


    // kakfka Event test
    public String sendTestMessage() {

        String message  = "Hello, Kafka! This is a test message from ProductService.";

        kafkaTemplate.send("product-test-topic", message);


        return "Message sent to Kafka topic: " + message;
    }

    // get all products
    public List<ProductDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        List<ProductDTO> productDTOS = new ArrayList<>();
        for (Product product : products) {
            ProductDTO productDTO = ProductDTO.builder().id(product.getId())
                    .name(product.getName())
                    .description(product.getDescription())
                    .price(product.getPrice())
                    .categoryName(product.getName())
                    .sku(product.getSku())
                    .build();
            productDTOS.add(productDTO);
        }

        return productDTOS;
    }

    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        return ProductDTO.builder().id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .categoryName(product.getName())
                .sku(product.getSku())
                .build();
    }

    // create product
    public ProductDTO createProduct(CreateProductDTO createProductDTO) {
        Product product = new Product();
        product.setName(createProductDTO.getName());
        product.setDescription(createProductDTO.getDescription());
        product.setPrice(createProductDTO.getPrice());
        product.setSku(createProductDTO.getSku());
        product.setStockQuantity(createProductDTO.getStockQuantity());
        product.setCategory(categoryRepository.findById(createProductDTO.getCategoryId()).orElse(null));
        Product savedProduct = productRepository.save(product);
        return ProductDTO.builder().id(savedProduct.getId())
                .name(savedProduct.getName())
                .description(savedProduct.getDescription())
                .price(savedProduct.getPrice())
                .categoryName(savedProduct.getCategory() != null ? savedProduct.getCategory().getName() : null)
                .sku(savedProduct.getSku())
                .build();
    }

    public ProductDTO updateProductPrice(Long productId, Double newPrice) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
        product.setPrice(newPrice);
        Product updatedProduct = productRepository.save(product);
        return ProductDTO.builder().id(updatedProduct.getId())
                .name(updatedProduct.getName())
                .description(updatedProduct.getDescription())
                .price(updatedProduct.getPrice())
                .categoryName(updatedProduct.getCategory() != null ? updatedProduct.getCategory().getName() : null)
                .sku(updatedProduct.getSku())
                .build();
    }

    // update inventory decrement and increment methods
    @Transactional
    public TransactionDTO incrementInventory(UpdateProductInventoryDTO updateProductInventoryDTO) {
        // Fetch the product
        Product product = productRepository.findById(updateProductInventoryDTO.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + updateProductInventoryDTO.getProductId()));

        // Increment the stock quantity
        product.setStockQuantity(product.getStockQuantity() + updateProductInventoryDTO.getQuantity());
        Product savedProduct = productRepository.save(product);

        // Create an inventory transaction
        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setProduct(savedProduct);
        transaction.setType(TransactionType.ADD.name());
        transaction.setQuantity(updateProductInventoryDTO.getQuantity());
        transaction.setTimestamp(java.time.LocalDateTime.now());
        InventoryTransaction savedTransaction = inventoryTransactionRepository.save(transaction);

        // Return the transaction details
        return TransactionDTO.builder().transactionId(savedTransaction.getId()).description("Inventory incremented")
                .productId(savedProduct.getId())
                .type(TransactionType.ADD.name())
                .build();

    }

    public TransactionDTO decrementInventory(UpdateProductInventoryDTO updateProductInventoryDTO) {
        // Fetch the product
        Product product = productRepository.findById(updateProductInventoryDTO.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + updateProductInventoryDTO.getProductId()));

        // Decrement the stock quantity
        if (product.getStockQuantity() < updateProductInventoryDTO.getQuantity()) {
            throw new InsufficientStockException("Insufficient stock for product id: " + updateProductInventoryDTO.getProductId());
        }
        product.setStockQuantity(product.getStockQuantity() - updateProductInventoryDTO.getQuantity());
        Product savedProduct = productRepository.save(product);

        // Create an inventory transaction
        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setProduct(savedProduct);
        transaction.setType(TransactionType.REMOVE.name());
        transaction.setQuantity(updateProductInventoryDTO.getQuantity());
        transaction.setTimestamp(java.time.LocalDateTime.now());
        InventoryTransaction savedTransaction = inventoryTransactionRepository.save(transaction);

        // Return the transaction details
        return TransactionDTO.builder().transactionId(savedTransaction.getId()).description("Inventory decremented")
                .productId(savedProduct.getId())
                .type(TransactionType.REMOVE.name())
                .build();

    }


    // Get All Categories
    public List<CategoryDTO> getAllCategories(){
        List<CategoryDTO> categoryDTOS = new ArrayList<>();
        categoryRepository.findAll().forEach(category -> {
            CategoryDTO categoryDTO = CategoryDTO.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .description(category.getDescription())
                    .build();
            categoryDTOS.add(categoryDTO);
        });
        return categoryDTOS;
    }

    // CreateCategory
    public CategoryDTO createCategory(CreateCategoryDTO categoryDTO){
        Category category = new Category();
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());
        Category savedCategory = categoryRepository.save(category);
        return CategoryDTO.builder()
                .id(savedCategory.getId())
                .name(savedCategory.getName())
                .description(savedCategory.getDescription())
                .build();

    }


    public InventoryDTO getInventoryByProductId(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        InventoryDTO inventoryDTO = new InventoryDTO();
        inventoryDTO.setProductId(product.getId());
        inventoryDTO.setProductName(product.getName());
        inventoryDTO.setStockInventory(product.getStockQuantity());

        return inventoryDTO;
    }

    public CategoryDTO updateCategory(Long id, CreateCategoryDTO createCategoryDTO) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        category.setName(createCategoryDTO.getName());
        category.setDescription(createCategoryDTO.getDescription());
        Category savedCategory = categoryRepository.save(category);
        return CategoryDTO.builder()
                .id(savedCategory.getId())
                .name(savedCategory.getName())
                .description(savedCategory.getDescription())
                .build();
    }
}

package com.wassimlagnaoui.ecommerce.product_service.Service;

import com.wassimlagnaoui.common_events.DummyEvent;
import com.wassimlagnaoui.common_events.Events.ProductService.ProductUpdatedEvent;
import com.wassimlagnaoui.common_events.KafkaTopics;
import com.wassimlagnaoui.ecommerce.product_service.DTO.*;
import com.wassimlagnaoui.ecommerce.product_service.Domain.Category;
import com.wassimlagnaoui.ecommerce.product_service.Domain.InventoryTransaction;
import com.wassimlagnaoui.ecommerce.product_service.Domain.Product;
import com.wassimlagnaoui.ecommerce.product_service.Domain.TransactionType;
import com.wassimlagnaoui.ecommerce.product_service.Exception.CategoryNotFoundException;
import com.wassimlagnaoui.ecommerce.product_service.Exception.InsufficientStockException;
import com.wassimlagnaoui.ecommerce.product_service.Exception.KafkaEventNotSentException;
import com.wassimlagnaoui.ecommerce.product_service.Exception.ProductNotFoundException;
import com.wassimlagnaoui.ecommerce.product_service.Repository.CategoryRepository;
import com.wassimlagnaoui.ecommerce.product_service.Repository.InventoryTransactionRepository;
import com.wassimlagnaoui.ecommerce.product_service.Repository.ProductRepository;
import com.wassimlagnaoui.ecommerce.product_service.Util.Topics;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;

    // add KafkaTemplate
    @Autowired
    private  KafkaTemplate<String, Object> kafkaTemplate;


    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, InventoryTransactionRepository inventoryTransactionRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.inventoryTransactionRepository = inventoryTransactionRepository;
    }


    // kafka Event test
    public String sendTestMessage() {
        DummyEvent dummyEvent = DummyEvent.builder()
                .message("This is a test message from Product Service")
                .timestamp(java.time.LocalDateTime.now())
                .build();


        kafkaTemplate.send(KafkaTopics.TOPIC_PRODUCT_TEST, dummyEvent);


        return "Message sent to Kafka topic: Dummy Event with the message "+dummyEvent.getMessage();
    }

    // get all products
    @RateLimiter(name = "productListingRateLimiter", fallbackMethod = "getAllProductsFallback")
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
    // Fallback method for getAllProducts
    public List<ProductDTO> getAllProductsFallback(Throwable throwable) {
        // Fallback logic when the rate limit is exceeded
        return new ArrayList<>();
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
        product.setPrice(createProductDTO.getPrice().doubleValue());
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
    public TransactionDTO incrementInventory(Long productId,UpdateProductInventoryDTO updateProductInventoryDTO) {
        // Fetch the product
        Product product = productRepository.findById(productId)
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

    public TransactionDTO decrementInventory(Long productId,UpdateProductInventoryDTO updateProductInventoryDTO) {
        // Fetch the product
        Product product = productRepository.findById(productId)
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

    @Bulkhead(name = "inventoryBulkhead", fallbackMethod = "getInventoryByProductIdFallback")
    public InventoryDTO getInventoryByProductId(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        InventoryDTO inventoryDTO = new InventoryDTO();
        inventoryDTO.setProductId(product.getId());
        inventoryDTO.setProductName(product.getName());
        inventoryDTO.setStockInventory(product.getStockQuantity());
        inventoryDTO.setSku(product.getSku());

        return inventoryDTO;
    }
    // Fallback method for getInventoryByProductId
    public InventoryDTO getInventoryByProductIdFallback(Long id, Throwable throwable) {
        // Fallback logic when the bulkhead is full
        return InventoryDTO.builder()
                .productId(id)
                .productName("Unknown Product")
                .stockInventory(0)
                .build();
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

    // get bulk product names by ids
    public List<ProductDTO> getProductsByIds(List<Long> ids) {
        List<Product> products = productRepository.findAllById(ids);
        List<ProductDTO> productDTOS = new ArrayList<>();
        for (Product product : products) {
            ProductDTO productDTO = ProductDTO.builder().id(product.getId())
                    .name(product.getName())
                    .description(product.getDescription())
                    .price(product.getPrice())
                    .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                    .sku(product.getSku())
                    .build();
            productDTOS.add(productDTO);
        }
        return productDTOS;
    }


    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }

    public List<?> getProductsByCategoryId(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
        List<Product> products = productRepository.findByCategory(category);
        List<ProductDTO> productDTOS = new ArrayList<>();
        for (Product product : products) {
            ProductDTO productDTO = ProductDTO.builder().id(product.getId())
                    .name(product.getName())
                    .description(product.getDescription())
                    .price(product.getPrice())
                    .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                    .sku(product.getSku())
                    .build();
            productDTOS.add(productDTO);
        }
        return productDTOS;

    }

    public MessageDTO deleteCategory(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
        categoryRepository.delete(category);
        return MessageDTO.builder().message("Category deleted successfully").build();
    }

    @Retry(name = "kafkaRetry", fallbackMethod = "updateProductFallback")
    @CircuitBreaker(name = "kafkaCircuitBreaker", fallbackMethod = "updateProductFallback")
    public ProductDTO updateProduct(Long id, CreateProductDTO createProductDTO) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        // this is called from a patch endpoint so we need to check for null values
        if (createProductDTO.getName() != null) {
            product.setName(createProductDTO.getName());
        }
        if (createProductDTO.getDescription() != null) {
            product.setDescription(createProductDTO.getDescription());
        }
        if (createProductDTO.getPrice() != null) {
            product.setPrice(createProductDTO.getPrice().doubleValue());
        }

        if (createProductDTO.getSku() != null) {
            product.setSku(createProductDTO.getSku());
        }
        if (createProductDTO.getStockQuantity() != null) {
            product.setStockQuantity(createProductDTO.getStockQuantity());
        }
        if (createProductDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(createProductDTO.getCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + createProductDTO.getCategoryId()));
            product.setCategory(category);
        }
        Product savedProduct = productRepository.save(product);

        // send Kafka event for product update
        ProductUpdatedEvent productUpdatedEvent = ProductUpdatedEvent.builder()
                .productId(savedProduct.getId().toString())
                .name(savedProduct.getName())
                .price(BigDecimal.valueOf(savedProduct.getPrice()))
                .sku(savedProduct.getSku())
                .stockQuantity(savedProduct.getStockQuantity())
                .build();

        try {
            kafkaTemplate.send(Topics.TOPIC_PRODUCT_UPDATED, productUpdatedEvent).get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new KafkaEventNotSentException("Failed to send ProductUpdatedEvent to Kafka: " + e.getMessage());
        }


        return ProductDTO.builder().id(savedProduct.getId())
                .name(savedProduct.getName())
                .description(savedProduct.getDescription())
                .price(savedProduct.getPrice())
                .categoryName(savedProduct.getCategory() != null ? savedProduct.getCategory().getName() : null)
                .sku(savedProduct.getSku())
                .build();

    }

    // Fallback method for updateProduct when Kafka is down
    public ProductDTO updateProductFallback(Long id, CreateProductDTO createProductDTO, Throwable throwable) {
        // Fallback logic when Kafka is down
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        // this is called from a patch endpoint so we need to check for null values
        if (createProductDTO.getName() != null) {
            product.setName(createProductDTO.getName());
        }
        if (createProductDTO.getDescription() != null) {
            product.setDescription(createProductDTO.getDescription());
        }
        if (createProductDTO.getPrice() != null) {
            product.setPrice(createProductDTO.getPrice().doubleValue());
        }

        if (createProductDTO.getSku() != null) {
            product.setSku(createProductDTO.getSku());
        }
        if (createProductDTO.getStockQuantity() != null) {
            product.setStockQuantity(createProductDTO.getStockQuantity());
        }
        if (createProductDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(createProductDTO.getCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + createProductDTO.getCategoryId()));
            product.setCategory(category);
        }
        Product savedProduct = productRepository.save(product);

        return ProductDTO.builder().id(savedProduct.getId())
                .name(savedProduct.getName())
                .description(savedProduct.getDescription())
                .price(savedProduct.getPrice())
                .categoryName(savedProduct.getCategory() != null ? savedProduct.getCategory().getName() : null)
                .sku(savedProduct.getSku())
                .build();
    }
}

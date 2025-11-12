package com.wassimlagnaoui.ecommerce.product_service.Controller;

import com.wassimlagnaoui.ecommerce.product_service.DTO.CategoryDTO;
import com.wassimlagnaoui.ecommerce.product_service.DTO.CreateCategoryDTO;
import com.wassimlagnaoui.ecommerce.product_service.Repository.CategoryRepository;
import com.wassimlagnaoui.ecommerce.product_service.Service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

  private final ProductService productService;

    public CategoryController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        List<CategoryDTO> categories = productService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    // create Category
    @PostMapping("/create")
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CreateCategoryDTO createCategoryDTO) {
        CategoryDTO categoryDTO = productService.createCategory(createCategoryDTO);
        return ResponseEntity.ok(categoryDTO);
    }

    // get Category by id
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        CategoryDTO categoryDTO = productService.getCategoryById(id);
        return ResponseEntity.ok(categoryDTO);
    }



    // update Category
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id, @RequestBody CreateCategoryDTO createCategoryDTO) {
        CategoryDTO categoryDTO = productService.updateCategory(id, createCategoryDTO);
        return ResponseEntity.ok(categoryDTO);
    }


}

package com.dietetic.backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dietetic.backend.entity.Category;
import com.dietetic.backend.entity.dto.CategoryRequestDTO;
import com.dietetic.backend.entity.dto.CategoryResponseDTO;
import com.dietetic.backend.service.CategoryService;

@RestController
@RequestMapping("categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    // GET /categories
    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        List<CategoryResponseDTO> categoryDTOs = categories.stream()
                .map(CategoryResponseDTO::fromCategory)
                .toList();

        return ResponseEntity.ok(categoryDTOs);
    }

    // GET /categories/paged
    @GetMapping("/paged")
    public ResponseEntity<Page<CategoryResponseDTO>> getPagedCategories(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size) {

        Page<Category> categoryPage = categoryService.getPagedCategories(PageRequest.of(page, size));
        Page<CategoryResponseDTO> categoryDTOPage = categoryPage.map(CategoryResponseDTO::fromCategory);

        return ResponseEntity.ok(categoryDTOPage);
    }

    // GET /categories/{id}
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(CategoryResponseDTO.fromCategory(category));
    }

    // GET /categories/name/{name}
    @GetMapping("/name/{name}")
    public ResponseEntity<CategoryResponseDTO> getCategoryByName(@PathVariable String name) {
        Category category = categoryService.getCategoryByName(name);
        return ResponseEntity.ok(CategoryResponseDTO.fromCategory(category));
    }

    // POST /categories
    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody CategoryRequestDTO requestDTO) {
        Category savedCategory = categoryService.createCategory(requestDTO.getName(), requestDTO.getDescription());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("message", "Categoría " + savedCategory.getName() + " creada con éxito"));
    }

    // PUT /categories/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody CategoryRequestDTO requestDTO) {
        Category currentCategory = categoryService.getCategoryById(id);
        String name = requestDTO.getName() != null ? requestDTO.getName() : currentCategory.getName();
        String description = requestDTO.getName() != null ? requestDTO.getDescription() : currentCategory.getDescription();

        Category updatedCategory = categoryService.updateCategory(id, name, description);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Map.of("message", "Categoría " + updatedCategory.getName() + " actualizada con éxito"));
    }

    // DELETE /categories/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategoryById(id);
        return ResponseEntity.ok(Map.of("message", "Categoría eliminada con éxito"));
    }

}

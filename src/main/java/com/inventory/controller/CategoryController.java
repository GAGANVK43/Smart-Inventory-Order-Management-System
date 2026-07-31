package com.inventory.controller;

import com.inventory.dto.CategoryRequestDto;
import com.inventory.dto.CategoryResponseDto;
import com.inventory.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller exposing Category CRUD API endpoints.
 * 
 * Concept Explanations:
 * - @RestController: Specialized version of @Controller that returns serialized JSON response bodies.
 * - @RequestMapping: Defines base URL path mapping for endpoints in this controller (/categories).
 * - @Valid: Enforces Jakarta Bean Validation on incoming JSON body payload.
 * - Constructor Injection: Promotes immutability and ease of unit testing.
 */
@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Create a new Category
     */
    @PostMapping
    public ResponseEntity<CategoryResponseDto> createCategory(@Valid @RequestBody CategoryRequestDto categoryRequestDto) {
        CategoryResponseDto createdCategory = categoryService.createCategory(categoryRequestDto);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    /**
     * Update an existing Category by ID
     */
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequestDto categoryRequestDto) {
        CategoryResponseDto updatedCategory = categoryService.updateCategory(id, categoryRequestDto);
        return ResponseEntity.ok(updatedCategory);
    }

    /**
     * Get Category details by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> getCategoryById(@PathVariable Long id) {
        CategoryResponseDto category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    /**
     * Get all Categories
     */
    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories() {
        List<CategoryResponseDto> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * Delete Category by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("Category with ID " + id + " has been successfully deleted.");
    }
}

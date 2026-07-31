package com.inventory.service;

import com.inventory.dto.CategoryRequestDto;
import com.inventory.dto.CategoryResponseDto;

import java.util.List;

/**
 * Service Interface defining Category business operations.
 */
public interface CategoryService {

    CategoryResponseDto createCategory(CategoryRequestDto categoryRequestDto);

    CategoryResponseDto updateCategory(Long id, CategoryRequestDto categoryRequestDto);

    CategoryResponseDto getCategoryById(Long id);

    List<CategoryResponseDto> getAllCategories();

    void deleteCategory(Long id);
}

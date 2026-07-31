package com.inventory.service.impl;

import com.inventory.dto.CategoryRequestDto;
import com.inventory.dto.CategoryResponseDto;
import com.inventory.entity.Category;
import com.inventory.exception.CategoryNotFoundException;
import com.inventory.exception.ResourceAlreadyExistsException;
import com.inventory.repository.CategoryRepository;
import com.inventory.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of CategoryService interface containing business rules.
 * 
 * Concept Explanations:
 * - @Service: Marks this class as a Spring-managed Service Component.
 * - @Transactional: Manages database transaction boundaries automatically.
 * - Dependency Injection: Uses constructor injection for CategoryRepository.
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public CategoryResponseDto createCategory(CategoryRequestDto categoryRequestDto) {
        if (categoryRepository.existsByName(categoryRequestDto.getName())) {
            throw new ResourceAlreadyExistsException("Category with name '" + categoryRequestDto.getName() + "' already exists.");
        }

        Category category = new Category();
        category.setName(categoryRequestDto.getName());
        category.setDescription(categoryRequestDto.getDescription());

        Category savedCategory = categoryRepository.save(category);
        return mapToResponseDto(savedCategory);
    }

    @Override
    @Transactional
    public CategoryResponseDto updateCategory(Long id, CategoryRequestDto categoryRequestDto) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with ID: " + id));

        if (!existingCategory.getName().equalsIgnoreCase(categoryRequestDto.getName()) 
                && categoryRepository.existsByName(categoryRequestDto.getName())) {
            throw new ResourceAlreadyExistsException("Category with name '" + categoryRequestDto.getName() + "' already exists.");
        }

        existingCategory.setName(categoryRequestDto.getName());
        existingCategory.setDescription(categoryRequestDto.getDescription());

        Category updatedCategory = categoryRepository.save(existingCategory);
        return mapToResponseDto(updatedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponseDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with ID: " + id));
        return mapToResponseDto(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new CategoryNotFoundException("Category not found with ID: " + id);
        }
        categoryRepository.deleteById(id);
    }

    private CategoryResponseDto mapToResponseDto(Category category) {
        return new CategoryResponseDto(
                category.getId(),
                category.getName(),
                category.getDescription()
        );
    }
}

package com.inventory.repository;

import com.inventory.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA Repository for Category Entity.
 * 
 * Concept Explanations:
 * - JpaRepository: Provides out-of-the-box CRUD methods (save, findById, findAll, deleteById, etc.).
 * - Derived Query Methods: Spring Data JPA automatically generates SQL based on method naming convention 
 *   (e.g., findByName, existsByName).
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);

    boolean existsByName(String name);
}

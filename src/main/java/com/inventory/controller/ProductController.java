package com.inventory.controller;

import com.inventory.dto.ProductRequestDto;
import com.inventory.dto.ProductResponseDto;
import com.inventory.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST Controller exposing Product CRUD and search API endpoints.
 */
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Add a new Product
     */
    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@Valid @RequestBody ProductRequestDto productRequestDto) {
        ProductResponseDto createdProduct = productService.createProduct(productRequestDto);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    /**
     * Update an existing Product by ID
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDto productRequestDto) {
        ProductResponseDto updatedProduct = productService.updateProduct(id, productRequestDto);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * Get Product details by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long id) {
        ProductResponseDto product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    /**
     * Get all Products
     */
    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        List<ProductResponseDto> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * Search Products by Name or Description keyword
     */
    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDto>> searchProducts(@RequestParam String keyword) {
        List<ProductResponseDto> products = productService.searchProducts(keyword);
        return ResponseEntity.ok(products);
    }

    /**
     * Get Products by Category ID
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductResponseDto>> getProductsByCategory(@PathVariable Long categoryId) {
        List<ProductResponseDto> products = productService.getProductsByCategory(categoryId);
        return ResponseEntity.ok(products);
    }

    /**
     * Get Products by Supplier ID
     */
    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<ProductResponseDto>> getProductsBySupplier(@PathVariable Long supplierId) {
        List<ProductResponseDto> products = productService.getProductsBySupplier(supplierId);
        return ResponseEntity.ok(products);
    }

    /**
     * Filter Products by Price Range
     */
    @GetMapping("/price-range")
    public ResponseEntity<List<ProductResponseDto>> getProductsByPriceRange(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {
        List<ProductResponseDto> products = productService.getProductsByPriceRange(min, max);
        return ResponseEntity.ok(products);
    }

    /**
     * Delete Product by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Product with ID " + id + " has been successfully deleted.");
    }
}

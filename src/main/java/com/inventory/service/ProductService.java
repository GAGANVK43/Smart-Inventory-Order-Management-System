package com.inventory.service;

import com.inventory.dto.ProductRequestDto;
import com.inventory.dto.ProductResponseDto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service Interface defining Product business operations.
 */
public interface ProductService {

    ProductResponseDto createProduct(ProductRequestDto productRequestDto);

    ProductResponseDto updateProduct(Long id, ProductRequestDto productRequestDto);

    ProductResponseDto getProductById(Long id);

    List<ProductResponseDto> getAllProducts();

    void deleteProduct(Long id);

    List<ProductResponseDto> searchProducts(String keyword);

    List<ProductResponseDto> getProductsByCategory(Long categoryId);

    List<ProductResponseDto> getProductsBySupplier(Long supplierId);

    List<ProductResponseDto> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
}

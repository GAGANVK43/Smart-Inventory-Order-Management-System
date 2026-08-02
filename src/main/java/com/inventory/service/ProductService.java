package com.inventory.service;

import com.inventory.dto.ProductRequestDto;
import com.inventory.dto.ProductResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service Interface defining Product business operations.
 */
public interface ProductService {

    ProductResponseDto createProduct(ProductRequestDto productRequestDto);

    ProductResponseDto updateProduct(Long id, ProductRequestDto productRequestDto);

    ProductResponseDto getProductById(Long id);

    ProductResponseDto getProductBySku(String sku);

    ProductResponseDto getProductByBarcode(String barcode);

    List<ProductResponseDto> getAllProducts();

    Page<ProductResponseDto> getAllProductsPaginated(Pageable pageable);

    void deleteProduct(Long id);

    List<ProductResponseDto> searchProducts(String keyword);

    Page<ProductResponseDto> searchProductsPaginated(String keyword, Pageable pageable);

    List<ProductResponseDto> getProductsByCategory(Long categoryId);

    List<ProductResponseDto> getProductsBySupplier(Long supplierId);

    List<ProductResponseDto> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
}

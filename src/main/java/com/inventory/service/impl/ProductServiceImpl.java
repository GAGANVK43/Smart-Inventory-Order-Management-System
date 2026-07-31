package com.inventory.service.impl;

import com.inventory.dto.ProductRequestDto;
import com.inventory.dto.ProductResponseDto;
import com.inventory.entity.Category;
import com.inventory.entity.Product;
import com.inventory.entity.Supplier;
import com.inventory.exception.CategoryNotFoundException;
import com.inventory.exception.ProductNotFoundException;
import com.inventory.exception.ResourceAlreadyExistsException;
import com.inventory.exception.SupplierNotFoundException;
import com.inventory.repository.CategoryRepository;
import com.inventory.repository.ProductRepository;
import com.inventory.repository.SupplierRepository;
import com.inventory.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of ProductService containing core business rules.
 */
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, 
                               CategoryRepository categoryRepository, 
                               SupplierRepository supplierRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.supplierRepository = supplierRepository;
    }

    @Override
    @Transactional
    public ProductResponseDto createProduct(ProductRequestDto productRequestDto) {
        if (productRepository.findByName(productRequestDto.getName()).isPresent()) {
            throw new ResourceAlreadyExistsException("Product with name '" + productRequestDto.getName() + "' already exists.");
        }

        Category category = categoryRepository.findById(productRequestDto.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with ID: " + productRequestDto.getCategoryId()));

        Supplier supplier = supplierRepository.findById(productRequestDto.getSupplierId())
                .orElseThrow(() -> new SupplierNotFoundException("Supplier not found with ID: " + productRequestDto.getSupplierId()));

        Product product = new Product();
        product.setName(productRequestDto.getName());
        product.setDescription(productRequestDto.getDescription());
        product.setPrice(productRequestDto.getPrice());
        product.setQuantity(productRequestDto.getQuantity());
        product.setCategory(category);
        product.setSupplier(supplier);

        Product savedProduct = productRepository.save(product);
        return mapToResponseDto(savedProduct);
    }

    @Override
    @Transactional
    public ProductResponseDto updateProduct(Long id, ProductRequestDto productRequestDto) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));

        Category category = categoryRepository.findById(productRequestDto.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with ID: " + productRequestDto.getCategoryId()));

        Supplier supplier = supplierRepository.findById(productRequestDto.getSupplierId())
                .orElseThrow(() -> new SupplierNotFoundException("Supplier not found with ID: " + productRequestDto.getSupplierId()));

        existingProduct.setName(productRequestDto.getName());
        existingProduct.setDescription(productRequestDto.getDescription());
        existingProduct.setPrice(productRequestDto.getPrice());
        existingProduct.setQuantity(productRequestDto.getQuantity());
        existingProduct.setCategory(category);
        existingProduct.setSupplier(supplier);

        Product updatedProduct = productRepository.save(existingProduct);
        return mapToResponseDto(updatedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));
        return mapToResponseDto(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Product not found with ID: " + id);
        }
        productRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> searchProducts(String keyword) {
        return productRepository.searchProducts(keyword).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getProductsByCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new CategoryNotFoundException("Category not found with ID: " + categoryId);
        }
        return productRepository.findByCategoryId(categoryId).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getProductsBySupplier(Long supplierId) {
        if (!supplierRepository.existsById(supplierId)) {
            throw new SupplierNotFoundException("Supplier not found with ID: " + supplierId);
        }
        return productRepository.findBySupplierId(supplierId).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByPriceBetween(minPrice, maxPrice).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    private ProductResponseDto mapToResponseDto(Product product) {
        return new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                product.getCategory().getId(),
                product.getCategory().getName(),
                product.getSupplier().getId(),
                product.getSupplier().getName()
        );
    }
}

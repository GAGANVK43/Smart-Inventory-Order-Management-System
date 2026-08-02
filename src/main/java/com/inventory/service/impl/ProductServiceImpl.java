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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of ProductService containing SKU generation, soft deletion, and pagination logic.
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
        product.setReorderLevel(productRequestDto.getReorderLevel() != null ? productRequestDto.getReorderLevel() : 5);
        product.setCategory(category);
        product.setSupplier(supplier);
        product.setActive(true);

        // Generate SKU if not provided
        if (StringUtils.hasText(productRequestDto.getSku())) {
            if (productRepository.existsBySku(productRequestDto.getSku())) {
                throw new ResourceAlreadyExistsException("Product with SKU '" + productRequestDto.getSku() + "' already exists.");
            }
            product.setSku(productRequestDto.getSku());
        } else {
            product.setSku(generateSku(category.getName(), productRequestDto.getName()));
        }

        // Generate Barcode if not provided
        if (StringUtils.hasText(productRequestDto.getBarcode())) {
            if (productRepository.existsByBarcode(productRequestDto.getBarcode())) {
                throw new ResourceAlreadyExistsException("Product with Barcode '" + productRequestDto.getBarcode() + "' already exists.");
            }
            product.setBarcode(productRequestDto.getBarcode());
        } else {
            product.setBarcode("BC-" + System.currentTimeMillis());
        }

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
        if (productRequestDto.getReorderLevel() != null) {
            existingProduct.setReorderLevel(productRequestDto.getReorderLevel());
        }
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
    public ProductResponseDto getProductBySku(String sku) {
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with SKU: " + sku));
        return mapToResponseDto(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDto getProductByBarcode(String barcode) {
        Product product = productRepository.findByBarcode(barcode)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with Barcode: " + barcode));
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
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getAllProductsPaginated(Pageable pageable) {
        return productRepository.findAllByActiveTrue(pageable)
                .map(this::mapToResponseDto);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));
        // Soft Delete Flag
        product.setActive(false);
        productRepository.save(product);
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
    public Page<ProductResponseDto> searchProductsPaginated(String keyword, Pageable pageable) {
        return productRepository.searchProductsPaginated(keyword, pageable)
                .map(this::mapToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getProductsByCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new CategoryNotFoundException("Category not found with ID: " + categoryId);
        }
        return productRepository.findByCategoryIdAndActiveTrue(categoryId).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getProductsBySupplier(Long supplierId) {
        if (!supplierRepository.existsById(supplierId)) {
            throw new SupplierNotFoundException("Supplier not found with ID: " + supplierId);
        }
        return productRepository.findBySupplierIdAndActiveTrue(supplierId).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByPriceBetweenAndActiveTrue(minPrice, maxPrice).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    private String generateSku(String categoryName, String productName) {
        String catCode = categoryName.length() >= 3 ? categoryName.substring(0, 3).toUpperCase() : "CAT";
        String prdCode = productName.length() >= 3 ? productName.substring(0, 3).toUpperCase() : "PRD";
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "SKU-" + catCode + "-" + prdCode + "-" + uniqueSuffix;
    }

    private ProductResponseDto mapToResponseDto(Product product) {
        boolean isLowStock = product.getQuantity() <= product.getReorderLevel();

        return new ProductResponseDto(
                product.getId(),
                product.getSku(),
                product.getBarcode(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                product.getReorderLevel(),
                product.isActive(),
                isLowStock,
                product.getCategory().getId(),
                product.getCategory().getName(),
                product.getSupplier().getId(),
                product.getSupplier().getName()
        );
    }
}

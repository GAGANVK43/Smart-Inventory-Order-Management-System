package com.inventory.service;

import com.inventory.dto.ProductRequestDto;
import com.inventory.dto.ProductResponseDto;
import com.inventory.entity.Category;
import com.inventory.entity.Product;
import com.inventory.entity.Supplier;
import com.inventory.exception.ProductNotFoundException;
import com.inventory.exception.ResourceAlreadyExistsException;
import com.inventory.repository.CategoryRepository;
import com.inventory.repository.ProductRepository;
import com.inventory.repository.SupplierRepository;
import com.inventory.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Category category;
    private Supplier supplier;
    private Product product;
    private ProductRequestDto productRequestDto;

    @BeforeEach
    void setUp() {
        category = new Category(1L, "Electronics", "Devices and gadgets");
        supplier = new Supplier(1L, "TechCorp", "John", "tech@corp.com", "1234567890", "Tech St");

        product = new Product();
        product.setId(1L);
        product.setSku("SKU-ELE-LAP-1234");
        product.setName("Laptop");
        product.setPrice(new BigDecimal("999.99"));
        product.setQuantity(10);
        product.setReorderLevel(5);
        product.setCategory(category);
        product.setSupplier(supplier);
        product.setActive(true);

        productRequestDto = new ProductRequestDto("SKU-ELE-LAP-1234", "BC-123", "Laptop", "High-end laptop", new BigDecimal("999.99"), 10, 5, 1L, 1L);
    }

    @Test
    @DisplayName("Create Product - Success")
    void testCreateProduct_Success() {
        when(productRepository.findByName("Laptop")).thenReturn(Optional.empty());
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponseDto response = productService.createProduct(productRequestDto);

        assertNotNull(response);
        assertEquals("Laptop", response.getName());
        assertEquals(new BigDecimal("999.99"), response.getPrice());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Create Product - Duplicate Name Exception")
    void testCreateProduct_DuplicateName_ThrowsException() {
        when(productRepository.findByName("Laptop")).thenReturn(Optional.of(product));

        assertThrows(ResourceAlreadyExistsException.class, () -> productService.createProduct(productRequestDto));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Get Product by ID - Success")
    void testGetProductById_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductResponseDto response = productService.getProductById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Laptop", response.getName());
    }

    @Test
    @DisplayName("Get Product by ID - NotFound Exception")
    void testGetProductById_NotFound_ThrowsException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(99L));
    }

    @Test
    @DisplayName("Soft Delete Product - Success")
    void testDeleteProduct_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.deleteProduct(1L);

        assertFalse(product.isActive());
        verify(productRepository, times(1)).save(product);
    }
}

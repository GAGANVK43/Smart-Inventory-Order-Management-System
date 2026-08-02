package com.inventory.service.impl;

import com.inventory.dto.ProductResponseDto;
import com.inventory.dto.SupplierRequestDto;
import com.inventory.dto.SupplierResponseDto;
import com.inventory.entity.Product;
import com.inventory.entity.Supplier;
import com.inventory.exception.ResourceAlreadyExistsException;
import com.inventory.exception.SupplierNotFoundException;
import com.inventory.repository.ProductRepository;
import com.inventory.repository.SupplierRepository;
import com.inventory.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of SupplierService containing core business logic.
 */
@Service
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;

    @Autowired
    public SupplierServiceImpl(SupplierRepository supplierRepository, ProductRepository productRepository) {
        this.supplierRepository = supplierRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public SupplierResponseDto createSupplier(SupplierRequestDto supplierRequestDto) {
        if (supplierRepository.existsByEmail(supplierRequestDto.getEmail())) {
            throw new ResourceAlreadyExistsException("Supplier with email '" + supplierRequestDto.getEmail() + "' already exists.");
        }

        Supplier supplier = new Supplier();
        supplier.setName(supplierRequestDto.getName());
        supplier.setContactPerson(supplierRequestDto.getContactPerson());
        supplier.setEmail(supplierRequestDto.getEmail());
        supplier.setPhone(supplierRequestDto.getPhone());
        supplier.setAddress(supplierRequestDto.getAddress());
        supplier.setActive(true);

        Supplier savedSupplier = supplierRepository.save(supplier);
        return mapToResponseDto(savedSupplier);
    }

    @Override
    @Transactional
    public SupplierResponseDto updateSupplier(Long id, SupplierRequestDto supplierRequestDto) {
        Supplier existingSupplier = supplierRepository.findById(id)
                .orElseThrow(() -> new SupplierNotFoundException("Supplier not found with ID: " + id));

        if (!existingSupplier.getEmail().equalsIgnoreCase(supplierRequestDto.getEmail()) 
                && supplierRepository.existsByEmail(supplierRequestDto.getEmail())) {
            throw new ResourceAlreadyExistsException("Supplier with email '" + supplierRequestDto.getEmail() + "' already exists.");
        }

        existingSupplier.setName(supplierRequestDto.getName());
        existingSupplier.setContactPerson(supplierRequestDto.getContactPerson());
        existingSupplier.setEmail(supplierRequestDto.getEmail());
        existingSupplier.setPhone(supplierRequestDto.getPhone());
        existingSupplier.setAddress(supplierRequestDto.getAddress());

        Supplier updatedSupplier = supplierRepository.save(existingSupplier);
        return mapToResponseDto(updatedSupplier);
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierResponseDto getSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new SupplierNotFoundException("Supplier not found with ID: " + id));
        return mapToResponseDto(supplier);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierResponseDto> getAllSuppliers() {
        return supplierRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getSupplierProducts(Long supplierId) {
        if (!supplierRepository.existsById(supplierId)) {
            throw new SupplierNotFoundException("Supplier not found with ID: " + supplierId);
        }
        return productRepository.findBySupplierIdAndActiveTrue(supplierId).stream()
                .map(this::mapToProductResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new SupplierNotFoundException("Supplier not found with ID: " + id));
        supplier.setActive(false);
        supplierRepository.save(supplier);
    }

    private SupplierResponseDto mapToResponseDto(Supplier supplier) {
        return new SupplierResponseDto(
                supplier.getId(),
                supplier.getName(),
                supplier.getContactPerson(),
                supplier.getEmail(),
                supplier.getPhone(),
                supplier.getAddress(),
                supplier.isActive()
        );
    }

    private ProductResponseDto mapToProductResponseDto(Product product) {
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

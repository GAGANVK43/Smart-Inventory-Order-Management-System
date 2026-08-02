package com.inventory.service;

import com.inventory.dto.ProductResponseDto;
import com.inventory.dto.SupplierRequestDto;
import com.inventory.dto.SupplierResponseDto;

import java.util.List;

/**
 * Service Interface defining Supplier business operations.
 */
public interface SupplierService {

    SupplierResponseDto createSupplier(SupplierRequestDto supplierRequestDto);

    SupplierResponseDto updateSupplier(Long id, SupplierRequestDto supplierRequestDto);

    SupplierResponseDto getSupplierById(Long id);

    List<SupplierResponseDto> getAllSuppliers();

    List<ProductResponseDto> getSupplierProducts(Long supplierId);

    void deleteSupplier(Long id);
}

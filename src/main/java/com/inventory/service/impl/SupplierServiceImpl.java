package com.inventory.service.impl;

import com.inventory.dto.SupplierRequestDto;
import com.inventory.dto.SupplierResponseDto;
import com.inventory.entity.Supplier;
import com.inventory.exception.ResourceAlreadyExistsException;
import com.inventory.exception.SupplierNotFoundException;
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

    @Autowired
    public SupplierServiceImpl(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @Override
    @Transactional
    public SupplierResponseDto createSupplier(SupplierRequestDto supplierRequestDto) {
        if (supplierRepository.existsByEmail(supplierRequestDto.getEmail())) {
            throw new ResourceAlreadyExistsException("Supplier with email '" + supplierRequestDto.getEmail() + "' already exists.");
        }

        Supplier supplier = new Supplier();
        supplier.setName(supplierRequestDto.getName());
        supplier.setEmail(supplierRequestDto.getEmail());
        supplier.setPhone(supplierRequestDto.getPhone());

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
        existingSupplier.setEmail(supplierRequestDto.getEmail());
        existingSupplier.setPhone(supplierRequestDto.getPhone());

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
    @Transactional
    public void deleteSupplier(Long id) {
        if (!supplierRepository.existsById(id)) {
            throw new SupplierNotFoundException("Supplier not found with ID: " + id);
        }
        supplierRepository.deleteById(id);
    }

    private SupplierResponseDto mapToResponseDto(Supplier supplier) {
        return new SupplierResponseDto(
                supplier.getId(),
                supplier.getName(),
                supplier.getEmail(),
                supplier.getPhone()
        );
    }
}

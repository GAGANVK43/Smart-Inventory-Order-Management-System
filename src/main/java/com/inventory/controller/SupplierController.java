package com.inventory.controller;

import com.inventory.dto.SupplierRequestDto;
import com.inventory.dto.SupplierResponseDto;
import com.inventory.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller exposing Supplier CRUD API endpoints.
 */
@RestController
@RequestMapping("/suppliers")
@CrossOrigin
public class SupplierController {

    private final SupplierService supplierService;

    @Autowired
    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    /**
     * Create a new Supplier
     */
    @PostMapping
    public ResponseEntity<SupplierResponseDto> createSupplier(@Valid @RequestBody SupplierRequestDto supplierRequestDto) {
        SupplierResponseDto createdSupplier = supplierService.createSupplier(supplierRequestDto);
        return new ResponseEntity<>(createdSupplier, HttpStatus.CREATED);
    }

    /**
     * Update an existing Supplier by ID
     */
    @PutMapping("/{id}")
    public ResponseEntity<SupplierResponseDto> updateSupplier(
            @PathVariable Long id,
            @Valid @RequestBody SupplierRequestDto supplierRequestDto) {
        SupplierResponseDto updatedSupplier = supplierService.updateSupplier(id, supplierRequestDto);
        return ResponseEntity.ok(updatedSupplier);
    }

    /**
     * Get Supplier details by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<SupplierResponseDto> getSupplierById(@PathVariable Long id) {
        SupplierResponseDto supplier = supplierService.getSupplierById(id);
        return ResponseEntity.ok(supplier);
    }

    /**
     * Get all Suppliers
     */
    @GetMapping
    public ResponseEntity<List<SupplierResponseDto>> getAllSuppliers() {
        List<SupplierResponseDto> suppliers = supplierService.getAllSuppliers();
        return ResponseEntity.ok(suppliers);
    }

    /**
     * Delete Supplier by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.ok("Supplier with ID " + id + " has been successfully deleted.");
    }
}

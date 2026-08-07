package com.inventory.controller;

import com.inventory.dto.ProductResponseDto;
import com.inventory.dto.SupplierRequestDto;
import com.inventory.dto.SupplierResponseDto;
import com.inventory.dto.response.ApiResponse;
import com.inventory.service.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller exposing Supplier CRUD and vendor-product mapping endpoints.
 */
@RestController
@RequestMapping("/suppliers")
@CrossOrigin
@Tag(name = "Supplier & Vendor Management", description = "Endpoints for managing vendors, contact details, and supplier-product catalog mappings")
public class SupplierController {

    private final SupplierService supplierService;

    @Autowired
    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create Supplier", description = "Add a new vendor with contact details and address")
    public ResponseEntity<ApiResponse<SupplierResponseDto>> createSupplier(@Valid @RequestBody SupplierRequestDto supplierRequestDto) {
        SupplierResponseDto createdSupplier = supplierService.createSupplier(supplierRequestDto);
        return new ResponseEntity<>(ApiResponse.success(createdSupplier, "Supplier created successfully"), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Update Supplier", description = "Update vendor contact details by ID")
    public ResponseEntity<ApiResponse<SupplierResponseDto>> updateSupplier(
            @PathVariable Long id,
            @Valid @RequestBody SupplierRequestDto supplierRequestDto) {
        SupplierResponseDto updatedSupplier = supplierService.updateSupplier(id, supplierRequestDto);
        return ResponseEntity.ok(ApiResponse.success(updatedSupplier, "Supplier updated successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Supplier by ID", description = "Retrieve vendor details by ID")
    public ResponseEntity<ApiResponse<SupplierResponseDto>> getSupplierById(@PathVariable Long id) {
        SupplierResponseDto supplier = supplierService.getSupplierById(id);
        return ResponseEntity.ok(ApiResponse.success(supplier));
    }

    @GetMapping
    @Operation(summary = "Get All Suppliers", description = "Retrieve all registered vendors in system")
    public ResponseEntity<ApiResponse<List<SupplierResponseDto>>> getAllSuppliers() {
        List<SupplierResponseDto> suppliers = supplierService.getAllSuppliers();
        return ResponseEntity.ok(ApiResponse.success(suppliers));
    }

    @GetMapping("/{id}/products")
    @Operation(summary = "Get Supplier Products", description = "Retrieve all products supplied by a specific vendor ID")
    public ResponseEntity<ApiResponse<List<ProductResponseDto>>> getSupplierProducts(@PathVariable Long id) {
        List<ProductResponseDto> products = supplierService.getSupplierProducts(id);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft Delete Supplier", description = "Mark vendor as inactive - Requires ROLE_ADMIN")
    public ResponseEntity<ApiResponse<String>> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.ok(ApiResponse.success("Supplier with ID " + id + " has been successfully soft-deleted."));
    }
}

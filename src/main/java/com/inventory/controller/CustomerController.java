package com.inventory.controller;

import com.inventory.dto.CustomerRequestDto;
import com.inventory.dto.CustomerResponseDto;
import com.inventory.dto.OrderResponseDto;
import com.inventory.dto.response.ApiResponse;
import com.inventory.service.CustomerService;
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
 * REST Controller exposing Customer CRUD, address management, and order history endpoints.
 */
@RestController
@RequestMapping("/customers")
@CrossOrigin
@Tag(name = "Customer Management & Order History", description = "Endpoints for managing customer profiles, shipping/billing addresses, and purchase history")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN')")
    @Operation(summary = "Create Customer Profile", description = "Register a new customer with contact information and addresses")
    public ResponseEntity<ApiResponse<CustomerResponseDto>> createCustomer(@Valid @RequestBody CustomerRequestDto customerRequestDto) {
        CustomerResponseDto createdCustomer = customerService.createCustomer(customerRequestDto);
        return new ResponseEntity<>(ApiResponse.success(createdCustomer, "Customer created successfully"), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN')")
    @Operation(summary = "Update Customer Profile", description = "Update customer contact and address details by ID")
    public ResponseEntity<ApiResponse<CustomerResponseDto>> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequestDto customerRequestDto) {
        CustomerResponseDto updatedCustomer = customerService.updateCustomer(id, customerRequestDto);
        return ResponseEntity.ok(ApiResponse.success(updatedCustomer, "Customer updated successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN')")
    @Operation(summary = "Get Customer Details", description = "Retrieve customer profile, lifetime spend metrics, and address details by ID")
    public ResponseEntity<ApiResponse<CustomerResponseDto>> getCustomerById(@PathVariable Long id) {
        CustomerResponseDto customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(ApiResponse.success(customer));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN')")
    @Operation(summary = "Get All Customers", description = "Retrieve list of all registered customers")
    public ResponseEntity<ApiResponse<List<CustomerResponseDto>>> getAllCustomers() {
        List<CustomerResponseDto> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(ApiResponse.success(customers));
    }

    @GetMapping("/{id}/orders")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN')")
    @Operation(summary = "Get Customer Order History", description = "Retrieve all orders placed by a specific customer ID")
    public ResponseEntity<ApiResponse<List<OrderResponseDto>>> getCustomerOrders(@PathVariable Long id) {
        List<OrderResponseDto> orders = customerService.getCustomerOrders(id);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft Delete Customer", description = "Mark customer record as inactive - Requires ROLE_ADMIN")
    public ResponseEntity<ApiResponse<String>> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok(ApiResponse.success("Customer with ID " + id + " has been successfully soft-deleted."));
    }
}

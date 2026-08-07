package com.inventory.controller;

import com.inventory.dto.OrderRequestDto;
import com.inventory.dto.OrderResponseDto;
import com.inventory.dto.request.OrderStatusUpdateRequestDto;
import com.inventory.dto.response.ApiResponse;
import com.inventory.service.OrderService;
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
 * REST Controller exposing Order processing, status updates, and cancellation endpoints.
 */
@RestController
@RequestMapping("/orders")
@CrossOrigin
@Tag(name = "Order Processing Engine", description = "Endpoints for order checkout, stock deduction, status state machine transitions, and order cancellation")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN')")
    @Operation(summary = "Place New Order", description = "Create order with line items, deduct real-time inventory stock, and log stock-out audit movement")
    public ResponseEntity<ApiResponse<OrderResponseDto>> createOrder(@Valid @RequestBody OrderRequestDto orderRequestDto) {
        OrderResponseDto createdOrder = orderService.createOrder(orderRequestDto);
        return new ResponseEntity<>(ApiResponse.success(createdOrder, "Order placed successfully"), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Order by ID", description = "Retrieve complete order details and item breakdown by ID")
    public ResponseEntity<ApiResponse<OrderResponseDto>> getOrderById(@PathVariable Long id) {
        OrderResponseDto order = orderService.getOrderById(id);
        return ResponseEntity.ok(ApiResponse.success(order));
    }

    @GetMapping
    @Operation(summary = "Get All Orders", description = "Retrieve list of all customer orders in system")
    public ResponseEntity<ApiResponse<List<OrderResponseDto>>> getAllOrders() {
        List<OrderResponseDto> orders = orderService.getAllOrders();
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get Orders by Customer", description = "Retrieve all orders placed by a specific customer ID")
    public ResponseEntity<ApiResponse<List<OrderResponseDto>>> getOrdersByCustomer(@PathVariable Long customerId) {
        List<OrderResponseDto> orders = orderService.getOrdersByCustomer(customerId);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @Operation(summary = "Update Order Status", description = "Transition order status (PENDING -> PROCESSING -> SHIPPED -> DELIVERED)")
    public ResponseEntity<ApiResponse<OrderResponseDto>> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusUpdateRequestDto statusDto) {
        OrderResponseDto updatedOrder = orderService.updateOrderStatus(id, statusDto.getStatus());
        return ResponseEntity.ok(ApiResponse.success(updatedOrder, "Order status updated successfully"));
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @Operation(summary = "Cancel Order", description = "Cancel pending/processing order, refund inventory stock, and log stock-in audit movement")
    public ResponseEntity<ApiResponse<OrderResponseDto>> cancelOrder(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        OrderResponseDto cancelledOrder = orderService.cancelOrder(id, reason);
        return ResponseEntity.ok(ApiResponse.success(cancelledOrder, "Order cancelled and stock refunded successfully"));
    }
}

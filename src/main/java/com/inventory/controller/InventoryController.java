package com.inventory.controller;

import com.inventory.dto.ProductResponseDto;
import com.inventory.dto.request.StockAdjustmentRequestDto;
import com.inventory.dto.response.ApiResponse;
import com.inventory.dto.response.InventoryMovementResponseDto;
import com.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
@CrossOrigin
@Tag(name = "Inventory & Stock Movement Management", description = "Endpoints for stock in/out adjustments, low-stock summary alerts, and stock audit history")
public class InventoryController {

    private final InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/adjust")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Adjust Product Stock", description = "Record stock-in, stock-out, or manual stock adjustment with audit tracking")
    public ResponseEntity<ApiResponse<InventoryMovementResponseDto>> adjustStock(
            @Valid @RequestBody StockAdjustmentRequestDto requestDto,
            Authentication authentication) {

        String username = authentication != null ? authentication.getName() : "SYSTEM";
        InventoryMovementResponseDto movement = inventoryService.adjustStock(requestDto, username);
        return ResponseEntity.ok(ApiResponse.success(movement, "Stock adjustment recorded successfully"));
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN')")
    @Operation(summary = "Get Low Stock Products", description = "Retrieve products whose quantity is at or below their reorder level threshold")
    public ResponseEntity<ApiResponse<List<ProductResponseDto>>> getLowStockProducts() {
        List<ProductResponseDto> lowStockProducts = inventoryService.getLowStockProducts();
        return ResponseEntity.ok(ApiResponse.success(lowStockProducts));
    }

    @GetMapping("/movements/product/{productId}")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN')")
    @Operation(summary = "Get Movement History by Product", description = "Retrieve stock movement audit trail for a specific product")
    public ResponseEntity<ApiResponse<List<InventoryMovementResponseDto>>> getMovementHistoryByProduct(@PathVariable Long productId) {
        List<InventoryMovementResponseDto> history = inventoryService.getMovementHistoryByProduct(productId);
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    @GetMapping("/movements")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @Operation(summary = "Get All Movement Logs", description = "Retrieve global inventory movement audit logs")
    public ResponseEntity<ApiResponse<List<InventoryMovementResponseDto>>> getAllMovementsHistory() {
        List<InventoryMovementResponseDto> history = inventoryService.getAllMovementsHistory();
        return ResponseEntity.ok(ApiResponse.success(history));
    }
}

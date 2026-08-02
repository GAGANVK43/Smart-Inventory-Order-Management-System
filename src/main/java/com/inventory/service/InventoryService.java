package com.inventory.service;

import com.inventory.dto.ProductResponseDto;
import com.inventory.dto.request.StockAdjustmentRequestDto;
import com.inventory.dto.response.InventoryMovementResponseDto;

import java.util.List;

public interface InventoryService {
    InventoryMovementResponseDto adjustStock(StockAdjustmentRequestDto requestDto, String performedBy);
    List<ProductResponseDto> getLowStockProducts();
    List<InventoryMovementResponseDto> getMovementHistoryByProduct(Long productId);
    List<InventoryMovementResponseDto> getAllMovementsHistory();
}

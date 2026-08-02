package com.inventory.dto.response;

import com.inventory.enums.MovementType;
import java.time.LocalDateTime;

public class InventoryMovementResponseDto {

    private Long id;
    private Long productId;
    private String productName;
    private String sku;
    private MovementType movementType;
    private Integer quantityChanged;
    private Integer stockAfter;
    private String reason;
    private String performedBy;
    private LocalDateTime createdAt;

    public InventoryMovementResponseDto() {
    }

    public InventoryMovementResponseDto(Long id, Long productId, String productName, String sku, MovementType movementType, Integer quantityChanged, Integer stockAfter, String reason, String performedBy, LocalDateTime createdAt) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.sku = sku;
        this.movementType = movementType;
        this.quantityChanged = quantityChanged;
        this.stockAfter = stockAfter;
        this.reason = reason;
        this.performedBy = performedBy;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public MovementType getMovementType() {
        return movementType;
    }

    public void setMovementType(MovementType movementType) {
        this.movementType = movementType;
    }

    public Integer getQuantityChanged() {
        return quantityChanged;
    }

    public void setQuantityChanged(Integer quantityChanged) {
        this.quantityChanged = quantityChanged;
    }

    public Integer getStockAfter() {
        return stockAfter;
    }

    public void setStockAfter(Integer stockAfter) {
        this.stockAfter = stockAfter;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

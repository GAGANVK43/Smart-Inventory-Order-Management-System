package com.inventory.dto.request;

import com.inventory.enums.MovementType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class StockAdjustmentRequestDto {

    @NotNull(message = "Product ID is mandatory")
    private Long productId;

    @NotNull(message = "Movement type is mandatory")
    private MovementType movementType;

    @NotNull(message = "Quantity change is mandatory")
    @Positive(message = "Quantity must be a positive integer greater than zero")
    private Integer quantity;

    @Size(max = 255, message = "Reason cannot exceed 255 characters")
    private String reason;

    public StockAdjustmentRequestDto() {
    }

    public StockAdjustmentRequestDto(Long productId, MovementType movementType, Integer quantity, String reason) {
        this.productId = productId;
        this.movementType = movementType;
        this.quantity = quantity;
        this.reason = reason;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public MovementType getMovementType() {
        return movementType;
    }

    public void setMovementType(MovementType movementType) {
        this.movementType = movementType;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}

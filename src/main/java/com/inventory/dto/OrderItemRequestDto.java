package com.inventory.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO representing an individual item line in an order request payload.
 */
public class OrderItemRequestDto {

    @NotNull(message = "Product ID is mandatory")
    private Long productId;

    @NotNull(message = "Quantity is mandatory")
    @Positive(message = "Quantity must be a positive integer greater than zero")
    private Integer quantity;

    public OrderItemRequestDto() {
    }

    public OrderItemRequestDto(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}

package com.inventory.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Data Transfer Object (DTO) for creating an Order with multiple line items.
 */
public class OrderRequestDto {

    @NotNull(message = "Customer ID is mandatory")
    private Long customerId;

    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<OrderItemRequestDto> items;

    public OrderRequestDto() {
    }

    public OrderRequestDto(Long customerId, List<OrderItemRequestDto> items) {
        this.customerId = customerId;
        this.items = items;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public List<OrderItemRequestDto> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequestDto> items) {
        this.items = items;
    }
}

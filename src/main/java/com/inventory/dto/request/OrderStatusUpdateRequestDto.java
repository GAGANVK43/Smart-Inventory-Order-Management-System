package com.inventory.dto.request;

import com.inventory.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

public class OrderStatusUpdateRequestDto {

    @NotNull(message = "Status is mandatory")
    private OrderStatus status;

    private String reason;

    public OrderStatusUpdateRequestDto() {
    }

    public OrderStatusUpdateRequestDto(OrderStatus status, String reason) {
        this.status = status;
        this.reason = reason;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}

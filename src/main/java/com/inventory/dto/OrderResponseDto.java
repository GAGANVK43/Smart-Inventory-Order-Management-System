package com.inventory.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object (DTO) for Order API responses.
 */
public class OrderResponseDto {

    private Long id;
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private BigDecimal totalAmount;
    private LocalDateTime orderDate;
    private List<OrderItemResponseDto> orderItems;

    public OrderResponseDto() {
    }

    public OrderResponseDto(Long id, Long customerId, String customerName, String customerEmail, BigDecimal totalAmount, LocalDateTime orderDate, List<OrderItemResponseDto> orderItems) {
        this.id = id;
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
        this.orderItems = orderItems;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public List<OrderItemResponseDto> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemResponseDto> orderItems) {
        this.orderItems = orderItems;
    }
}

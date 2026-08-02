package com.inventory.dto;

import java.math.BigDecimal;

/**
 * Data Transfer Object (DTO) for Customer API responses.
 */
public class CustomerResponseDto {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String shippingAddress;
    private String billingAddress;
    private BigDecimal totalSpent;
    private Integer orderCount;
    private boolean active;

    public CustomerResponseDto() {
    }

    public CustomerResponseDto(Long id, String name, String email, String phone, String shippingAddress, String billingAddress, BigDecimal totalSpent, Integer orderCount, boolean active) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.shippingAddress = shippingAddress;
        this.billingAddress = billingAddress;
        this.totalSpent = totalSpent;
        this.orderCount = orderCount;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }

    public BigDecimal getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(BigDecimal totalSpent) {
        this.totalSpent = totalSpent;
    }

    public Integer getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Integer orderCount) {
        this.orderCount = orderCount;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

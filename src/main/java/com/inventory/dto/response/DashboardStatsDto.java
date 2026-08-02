package com.inventory.dto.response;

import java.math.BigDecimal;

public class DashboardStatsDto {

    private BigDecimal totalRevenue;
    private long totalOrders;
    private long totalProducts;
    private long totalCustomers;
    private long lowStockCount;

    public DashboardStatsDto() {
    }

    public DashboardStatsDto(BigDecimal totalRevenue, long totalOrders, long totalProducts, long totalCustomers, long lowStockCount) {
        this.totalRevenue = totalRevenue != null ? totalRevenue : BigDecimal.ZERO;
        this.totalOrders = totalOrders;
        this.totalProducts = totalProducts;
        this.totalCustomers = totalCustomers;
        this.lowStockCount = lowStockCount;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public long getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(long totalProducts) {
        this.totalProducts = totalProducts;
    }

    public long getTotalCustomers() {
        return totalCustomers;
    }

    public void setTotalCustomers(long totalCustomers) {
        this.totalCustomers = totalCustomers;
    }

    public long getLowStockCount() {
        return lowStockCount;
    }

    public void setLowStockCount(long lowStockCount) {
        this.lowStockCount = lowStockCount;
    }
}

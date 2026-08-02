package com.inventory.dto.response;

import java.math.BigDecimal;

public class RevenueTrendDto {

    private String datePeriod;
    private BigDecimal revenue;
    private long orderCount;

    public RevenueTrendDto() {
    }

    public RevenueTrendDto(String datePeriod, BigDecimal revenue, long orderCount) {
        this.datePeriod = datePeriod;
        this.revenue = revenue != null ? revenue : BigDecimal.ZERO;
        this.orderCount = orderCount;
    }

    public String getDatePeriod() {
        return datePeriod;
    }

    public void setDatePeriod(String datePeriod) {
        this.datePeriod = datePeriod;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    public long getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(long orderCount) {
        this.orderCount = orderCount;
    }
}

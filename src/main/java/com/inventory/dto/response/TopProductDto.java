package com.inventory.dto.response;

import java.math.BigDecimal;

public class TopProductDto {

    private Long productId;
    private String productName;
    private String sku;
    private String categoryName;
    private long totalUnitsSold;
    private BigDecimal totalRevenueGenerated;

    public TopProductDto() {
    }

    public TopProductDto(Long productId, String productName, String sku, String categoryName, long totalUnitsSold, BigDecimal totalRevenueGenerated) {
        this.productId = productId;
        this.productName = productName;
        this.sku = sku;
        this.categoryName = categoryName;
        this.totalUnitsSold = totalUnitsSold;
        this.totalRevenueGenerated = totalRevenueGenerated != null ? totalRevenueGenerated : BigDecimal.ZERO;
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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public long getTotalUnitsSold() {
        return totalUnitsSold;
    }

    public void setTotalUnitsSold(long totalUnitsSold) {
        this.totalUnitsSold = totalUnitsSold;
    }

    public BigDecimal getTotalRevenueGenerated() {
        return totalRevenueGenerated;
    }

    public void setTotalRevenueGenerated(BigDecimal totalRevenueGenerated) {
        this.totalRevenueGenerated = totalRevenueGenerated;
    }
}

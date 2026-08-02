package com.inventory.dto.response;

public class StockHealthDto {

    private String categoryName;
    private long inStockCount;
    private long lowStockCount;
    private long outOfStockCount;

    public StockHealthDto() {
    }

    public StockHealthDto(String categoryName, long inStockCount, long lowStockCount, long outOfStockCount) {
        this.categoryName = categoryName;
        this.inStockCount = inStockCount;
        this.lowStockCount = lowStockCount;
        this.outOfStockCount = outOfStockCount;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public long getInStockCount() {
        return inStockCount;
    }

    public void setInStockCount(long inStockCount) {
        this.inStockCount = inStockCount;
    }

    public long getLowStockCount() {
        return lowStockCount;
    }

    public void setLowStockCount(long lowStockCount) {
        this.lowStockCount = lowStockCount;
    }

    public long getOutOfStockCount() {
        return outOfStockCount;
    }

    public void setOutOfStockCount(long outOfStockCount) {
        this.outOfStockCount = outOfStockCount;
    }
}

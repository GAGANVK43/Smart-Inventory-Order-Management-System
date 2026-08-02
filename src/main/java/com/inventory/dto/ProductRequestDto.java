package com.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Data Transfer Object (DTO) for Product creation and update requests.
 */
public class ProductRequestDto {

    private String sku;

    private String barcode;

    @NotBlank(message = "Product name is mandatory")
    @Size(min = 2, max = 150, message = "Product name must be between 2 and 150 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotNull(message = "Price is mandatory")
    @Positive(message = "Price must be a positive value greater than zero")
    private BigDecimal price;

    @NotNull(message = "Quantity is mandatory")
    @PositiveOrZero(message = "Quantity must be zero or a positive integer")
    private Integer quantity;

    @PositiveOrZero(message = "Reorder level must be zero or positive")
    private Integer reorderLevel = 5;

    @NotNull(message = "Category ID is mandatory")
    private Long categoryId;

    @NotNull(message = "Supplier ID is mandatory")
    private Long supplierId;

    public ProductRequestDto() {
    }

    public ProductRequestDto(String sku, String barcode, String name, String description, BigDecimal price, Integer quantity, Integer reorderLevel, Long categoryId, Long supplierId) {
        this.sku = sku;
        this.barcode = barcode;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.reorderLevel = reorderLevel;
        this.categoryId = categoryId;
        this.supplierId = supplierId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(Integer reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }
}

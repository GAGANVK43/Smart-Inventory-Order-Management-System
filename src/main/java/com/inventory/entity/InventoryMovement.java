package com.inventory.entity;

import com.inventory.enums.MovementType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * InventoryMovement Entity mapping to 'inventory_movements' table for audit trail tracking.
 */
@Entity
@Table(
    name = "inventory_movements",
    indexes = {
        @Index(name = "idx_movement_product", columnList = "product_id"),
        @Index(name = "idx_movement_type", columnList = "movement_type"),
        @Index(name = "idx_movement_created", columnList = "created_at")
    }
)
public class InventoryMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false, length = 20)
    private MovementType movementType;

    @Column(name = "quantity_changed", nullable = false)
    private Integer quantityChanged;

    @Column(name = "stock_after", nullable = false)
    private Integer stockAfter;

    @Column(length = 255)
    private String reason;

    @Column(name = "performed_by", length = 100)
    private String performedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public InventoryMovement() {
    }

    public InventoryMovement(Product product, MovementType movementType, Integer quantityChanged, Integer stockAfter, String reason, String performedBy) {
        this.product = product;
        this.movementType = movementType;
        this.quantityChanged = quantityChanged;
        this.stockAfter = stockAfter;
        this.reason = reason;
        this.performedBy = performedBy;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public MovementType getMovementType() {
        return movementType;
    }

    public void setMovementType(MovementType movementType) {
        this.movementType = movementType;
    }

    public Integer getQuantityChanged() {
        return quantityChanged;
    }

    public void setQuantityChanged(Integer quantityChanged) {
        this.quantityChanged = quantityChanged;
    }

    public Integer getStockAfter() {
        return stockAfter;
    }

    public void setStockAfter(Integer stockAfter) {
        this.stockAfter = stockAfter;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

package com.inventory.enums;

/**
 * Enumeration defining Order status lifecycle state machine.
 */
public enum OrderStatus {
    PENDING,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED
}

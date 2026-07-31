package com.inventory.repository;

import com.inventory.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for OrderItem persistence operations.
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}

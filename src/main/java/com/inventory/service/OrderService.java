package com.inventory.service;

import com.inventory.dto.OrderRequestDto;
import com.inventory.dto.OrderResponseDto;
import com.inventory.enums.OrderStatus;

import java.util.List;

/**
 * Service Interface defining Order business operations and state machine transitions.
 */
public interface OrderService {

    OrderResponseDto createOrder(OrderRequestDto orderRequestDto);

    OrderResponseDto getOrderById(Long id);

    List<OrderResponseDto> getAllOrders();

    List<OrderResponseDto> getOrdersByCustomer(Long customerId);

    OrderResponseDto updateOrderStatus(Long orderId, OrderStatus status);

    OrderResponseDto cancelOrder(Long orderId, String reason);
}

package com.inventory.service;

import com.inventory.dto.OrderRequestDto;
import com.inventory.dto.OrderResponseDto;

import java.util.List;

/**
 * Service Interface defining Order business operations.
 */
public interface OrderService {

    OrderResponseDto createOrder(OrderRequestDto orderRequestDto);

    OrderResponseDto getOrderById(Long id);

    List<OrderResponseDto> getAllOrders();

    List<OrderResponseDto> getOrdersByCustomer(Long customerId);
}

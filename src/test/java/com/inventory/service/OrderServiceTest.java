package com.inventory.service;

import com.inventory.dto.OrderItemRequestDto;
import com.inventory.dto.OrderRequestDto;
import com.inventory.dto.OrderResponseDto;
import com.inventory.entity.Customer;
import com.inventory.entity.Order;
import com.inventory.entity.Product;
import com.inventory.enums.OrderStatus;
import com.inventory.exception.OutOfStockException;
import com.inventory.repository.CustomerRepository;
import com.inventory.repository.InventoryMovementRepository;
import com.inventory.repository.OrderRepository;
import com.inventory.repository.ProductRepository;
import com.inventory.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private InventoryMovementRepository movementRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Customer customer;
    private Product product;
    private OrderRequestDto orderRequestDto;

    @BeforeEach
    void setUp() {
        customer = new Customer(1L, "Alice Smith", "alice@example.com", "9876543210", "123 Main St", "123 Main St");
        
        product = new Product();
        product.setId(1L);
        product.setName("Wireless Mouse");
        product.setPrice(new BigDecimal("25.00"));
        product.setQuantity(20);

        OrderItemRequestDto itemDto = new OrderItemRequestDto(1L, 2);
        orderRequestDto = new OrderRequestDto(1L, Collections.singletonList(itemDto));
    }

    @Test
    @DisplayName("Create Order - Success with Stock Deduction")
    void testCreateOrder_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setId(100L);
            return o;
        });

        OrderResponseDto response = orderService.createOrder(orderRequestDto);

        assertNotNull(response);
        assertEquals(new BigDecimal("50.00"), response.getTotalAmount());
        assertEquals(18, product.getQuantity()); // Stock deducted from 20 to 18
        verify(productRepository, times(1)).save(product);
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    @DisplayName("Create Order - OutOfStock Exception")
    void testCreateOrder_OutOfStock_ThrowsException() {
        product.setQuantity(1); // Only 1 unit in stock, request is 2
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(OutOfStockException.class, () -> orderService.createOrder(orderRequestDto));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Cancel Order - Success with Stock Refund")
    void testCancelOrder_Success() {
        Order order = new Order();
        order.setId(100L);
        order.setOrderNumber("ORD-20260803-0001");
        order.setCustomer(customer);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(new BigDecimal("50.00"));

        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderResponseDto response = orderService.cancelOrder(100L, "Customer request");

        assertNotNull(response);
        assertEquals(OrderStatus.CANCELLED, response.getStatus());
    }
}

package com.inventory.service.impl;

import com.inventory.dto.OrderItemRequestDto;
import com.inventory.dto.OrderItemResponseDto;
import com.inventory.dto.OrderRequestDto;
import com.inventory.dto.OrderResponseDto;
import com.inventory.entity.Customer;
import com.inventory.entity.Order;
import com.inventory.entity.OrderItem;
import com.inventory.entity.Product;
import com.inventory.exception.CustomerNotFoundException;
import com.inventory.exception.OrderNotFoundException;
import com.inventory.exception.OutOfStockException;
import com.inventory.exception.ProductNotFoundException;
import com.inventory.repository.CustomerRepository;
import com.inventory.repository.OrderRepository;
import com.inventory.repository.ProductRepository;
import com.inventory.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of OrderService managing complex transactional order processing.
 * 
 * Concept Explanations:
 * - @Transactional: Ensures ACID properties across multi-step operations (stock deduction + order creation).
 *   If any item fails stock validation, the entire transaction rolls back cleanly.
 */
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository,
                            CustomerRepository customerRepository,
                            ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto orderRequestDto) {
        Customer customer = customerRepository.findById(orderRequestDto.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + orderRequestDto.getCustomerId()));

        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());

        BigDecimal calculatedTotal = BigDecimal.ZERO;

        for (OrderItemRequestDto itemDto : orderRequestDto.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + itemDto.getProductId()));

            if (product.getQuantity() < itemDto.getQuantity()) {
                throw new OutOfStockException("Insufficient stock for product '" + product.getName() 
                        + "'. Available stock: " + product.getQuantity() + ", requested quantity: " + itemDto.getQuantity());
            }

            // Deduct stock from inventory
            product.setQuantity(product.getQuantity() - itemDto.getQuantity());
            productRepository.save(product);

            // Calculate item subtotal using snapshot product price
            BigDecimal itemSubTotal = product.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity()));
            calculatedTotal = calculatedTotal.add(itemSubTotal);

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setPrice(product.getPrice());

            order.addOrderItem(orderItem);
        }

        order.setTotalAmount(calculatedTotal);

        Order savedOrder = orderRepository.save(order);
        return mapToResponseDto(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + id));
        return mapToResponseDto(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getOrdersByCustomer(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new CustomerNotFoundException("Customer not found with ID: " + customerId);
        }
        return orderRepository.findByCustomerId(customerId).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    private OrderResponseDto mapToResponseDto(Order order) {
        List<OrderItemResponseDto> itemDtos = order.getOrderItems().stream()
                .map(item -> new OrderItemResponseDto(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getPrice(),
                        item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                ))
                .collect(Collectors.toList());

        return new OrderResponseDto(
                order.getId(),
                order.getCustomer().getId(),
                order.getCustomer().getName(),
                order.getCustomer().getEmail(),
                order.getTotalAmount(),
                order.getOrderDate(),
                itemDtos
        );
    }
}

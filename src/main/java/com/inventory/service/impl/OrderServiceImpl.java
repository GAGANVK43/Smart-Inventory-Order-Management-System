package com.inventory.service.impl;

import com.inventory.dto.OrderItemRequestDto;
import com.inventory.dto.OrderItemResponseDto;
import com.inventory.dto.OrderRequestDto;
import com.inventory.dto.OrderResponseDto;
import com.inventory.entity.Customer;
import com.inventory.entity.InventoryMovement;
import com.inventory.entity.Order;
import com.inventory.entity.OrderItem;
import com.inventory.entity.Product;
import com.inventory.enums.MovementType;
import com.inventory.enums.OrderStatus;
import com.inventory.exception.CustomerNotFoundException;
import com.inventory.exception.OrderNotFoundException;
import com.inventory.exception.OutOfStockException;
import com.inventory.exception.ProductNotFoundException;
import com.inventory.repository.CustomerRepository;
import com.inventory.repository.InventoryMovementRepository;
import com.inventory.repository.OrderRepository;
import com.inventory.repository.ProductRepository;
import com.inventory.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of OrderService managing order placement, inventory deductions, status state machine, and cancellations.
 */
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final InventoryMovementRepository movementRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository,
                             CustomerRepository customerRepository,
                             ProductRepository productRepository,
                             InventoryMovementRepository movementRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.movementRepository = movementRepository;
    }

    @Override
    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto orderRequestDto) {
        Customer customer = customerRepository.findById(orderRequestDto.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + orderRequestDto.getCustomerId()));

        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setCustomer(customer);
        order.setStatus(OrderStatus.PENDING);
        order.setShippingAddress(customer.getShippingAddress());
        order.setBillingAddress(customer.getBillingAddress());
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
            int newQuantity = product.getQuantity() - itemDto.getQuantity();
            product.setQuantity(newQuantity);
            productRepository.save(product);

            // Audit movement log
            InventoryMovement movement = new InventoryMovement(
                    product,
                    MovementType.STOCK_OUT,
                    itemDto.getQuantity(),
                    newQuantity,
                    "Order Placement #" + order.getOrderNumber(),
                    "SYSTEM"
            );
            movementRepository.save(movement);

            // Calculate subtotal
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

        // Update Customer Spend Metrics
        customer.setTotalSpent(customer.getTotalSpent().add(calculatedTotal));
        customer.setOrderCount(customer.getOrderCount() + 1);
        customerRepository.save(customer);

        return mapToResponseDto(savedOrder);
    }

    @Override
    @Transactional
    public OrderResponseDto updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot change status of a CANCELLED order");
        }

        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        return mapToResponseDto(updatedOrder);
    }

    @Override
    @Transactional
    public OrderResponseDto cancelOrder(Long orderId, String reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));

        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.SHIPPED) {
            throw new IllegalStateException("Cannot cancel order with status '" + order.getStatus() + "'");
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            return mapToResponseDto(order); // Already cancelled
        }

        // Refund product stock
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            int restoredQuantity = product.getQuantity() + item.getQuantity();
            product.setQuantity(restoredQuantity);
            productRepository.save(product);

            // Audit movement log
            InventoryMovement movement = new InventoryMovement(
                    product,
                    MovementType.STOCK_IN,
                    item.getQuantity(),
                    restoredQuantity,
                    "Order Cancellation #" + order.getOrderNumber() + (reason != null ? " Reason: " + reason : ""),
                    "SYSTEM"
            );
            movementRepository.save(movement);
        }

        // Adjust Customer Spend Metrics
        Customer customer = order.getCustomer();
        BigDecimal adjustedSpend = customer.getTotalSpent().subtract(order.getTotalAmount());
        customer.setTotalSpent(adjustedSpend.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : adjustedSpend);
        customerRepository.save(customer);

        order.setStatus(OrderStatus.CANCELLED);
        Order cancelledOrder = orderRepository.save(order);
        return mapToResponseDto(cancelledOrder);
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

    private String generateOrderNumber() {
        String datePrefix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomSuffix = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "ORD-" + datePrefix + "-" + randomSuffix;
    }

    private OrderResponseDto mapToResponseDto(Order order) {
        List<OrderItemResponseDto> itemDtos = order.getOrderItems().stream()
                .map(item -> new OrderItemResponseDto(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getPrice(),
                        item.getPrice() != null && item.getQuantity() != null 
                                ? item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                                : BigDecimal.ZERO
                ))
                .collect(Collectors.toList());

        return new OrderResponseDto(
                order.getId(),
                order.getOrderNumber(),
                order.getCustomer().getId(),
                order.getCustomer().getName(),
                order.getCustomer().getEmail(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getShippingAddress(),
                order.getBillingAddress(),
                order.getOrderDate(),
                itemDtos
        );
    }
}

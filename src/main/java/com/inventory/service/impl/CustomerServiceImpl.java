package com.inventory.service.impl;

import com.inventory.dto.CustomerRequestDto;
import com.inventory.dto.CustomerResponseDto;
import com.inventory.dto.OrderItemResponseDto;
import com.inventory.dto.OrderResponseDto;
import com.inventory.entity.Customer;
import com.inventory.entity.Order;
import com.inventory.exception.CustomerNotFoundException;
import com.inventory.exception.ResourceAlreadyExistsException;
import com.inventory.repository.CustomerRepository;
import com.inventory.repository.OrderRepository;
import com.inventory.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of CustomerService containing customer management and purchase history mapping.
 */
@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository, OrderRepository orderRepository) {
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public CustomerResponseDto createCustomer(CustomerRequestDto customerRequestDto) {
        if (customerRepository.existsByEmail(customerRequestDto.getEmail())) {
            throw new ResourceAlreadyExistsException("Customer with email '" + customerRequestDto.getEmail() + "' already exists.");
        }

        Customer customer = new Customer();
        customer.setName(customerRequestDto.getName());
        customer.setEmail(customerRequestDto.getEmail());
        customer.setPhone(customerRequestDto.getPhone());
        customer.setShippingAddress(customerRequestDto.getShippingAddress());
        customer.setBillingAddress(customerRequestDto.getBillingAddress());
        customer.setActive(true);

        Customer savedCustomer = customerRepository.save(customer);
        return mapToResponseDto(savedCustomer);
    }

    @Override
    @Transactional
    public CustomerResponseDto updateCustomer(Long id, CustomerRequestDto customerRequestDto) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + id));

        if (!existingCustomer.getEmail().equalsIgnoreCase(customerRequestDto.getEmail()) 
                && customerRepository.existsByEmail(customerRequestDto.getEmail())) {
            throw new ResourceAlreadyExistsException("Customer with email '" + customerRequestDto.getEmail() + "' already exists.");
        }

        existingCustomer.setName(customerRequestDto.getName());
        existingCustomer.setEmail(customerRequestDto.getEmail());
        existingCustomer.setPhone(customerRequestDto.getPhone());
        existingCustomer.setShippingAddress(customerRequestDto.getShippingAddress());
        existingCustomer.setBillingAddress(customerRequestDto.getBillingAddress());

        Customer updatedCustomer = customerRepository.save(existingCustomer);
        return mapToResponseDto(updatedCustomer);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponseDto getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + id));
        return mapToResponseDto(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponseDto> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getCustomerOrders(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new CustomerNotFoundException("Customer not found with ID: " + customerId);
        }

        return orderRepository.findByCustomerId(customerId).stream()
                .map(this::mapToOrderResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + id));
        customer.setActive(false);
        customerRepository.save(customer);
    }

    private CustomerResponseDto mapToResponseDto(Customer customer) {
        return new CustomerResponseDto(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getShippingAddress(),
                customer.getBillingAddress(),
                customer.getTotalSpent(),
                customer.getOrderCount(),
                customer.isActive()
        );
    }

    private OrderResponseDto mapToOrderResponseDto(Order order) {
        List<OrderItemResponseDto> itemDtos = order.getOrderItems().stream()
                .map(item -> new OrderItemResponseDto(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getPrice(),
                        item.getPrice() != null && item.getQuantity() != null 
                                ? item.getPrice().multiply(java.math.BigDecimal.valueOf(item.getQuantity())) 
                                : java.math.BigDecimal.ZERO
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

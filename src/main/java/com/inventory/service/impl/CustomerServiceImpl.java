package com.inventory.service.impl;

import com.inventory.dto.CustomerRequestDto;
import com.inventory.dto.CustomerResponseDto;
import com.inventory.entity.Customer;
import com.inventory.exception.CustomerNotFoundException;
import com.inventory.exception.ResourceAlreadyExistsException;
import com.inventory.repository.CustomerRepository;
import com.inventory.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of CustomerService containing core business rules.
 */
@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
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
    @Transactional
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new CustomerNotFoundException("Customer not found with ID: " + id);
        }
        customerRepository.deleteById(id);
    }

    private CustomerResponseDto mapToResponseDto(Customer customer) {
        return new CustomerResponseDto(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone()
        );
    }
}

package com.inventory.service;

import com.inventory.dto.CustomerRequestDto;
import com.inventory.dto.CustomerResponseDto;

import java.util.List;

/**
 * Service Interface defining Customer business operations.
 */
public interface CustomerService {

    CustomerResponseDto createCustomer(CustomerRequestDto customerRequestDto);

    CustomerResponseDto updateCustomer(Long id, CustomerRequestDto customerRequestDto);

    CustomerResponseDto getCustomerById(Long id);

    List<CustomerResponseDto> getAllCustomers();

    void deleteCustomer(Long id);
}

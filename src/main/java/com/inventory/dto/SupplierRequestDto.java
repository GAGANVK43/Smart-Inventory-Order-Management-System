package com.inventory.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) for Supplier creation and update requests.
 */
public class SupplierRequestDto {

    @NotBlank(message = "Supplier name is mandatory")
    @Size(min = 2, max = 100, message = "Supplier name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Phone number is mandatory")
    @Pattern(regexp = "^[0-9+ -]{8,20}$", message = "Please provide a valid phone number (8 to 20 digits)")
    private String phone;

    public SupplierRequestDto() {
    }

    public SupplierRequestDto(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

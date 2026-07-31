package com.inventory.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom Exception thrown when a requested Supplier is not found.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class SupplierNotFoundException extends RuntimeException {

    public SupplierNotFoundException(String message) {
        super(message);
    }
}

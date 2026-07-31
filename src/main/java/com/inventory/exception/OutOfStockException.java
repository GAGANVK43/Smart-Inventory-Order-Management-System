package com.inventory.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom Exception thrown when requested product quantity exceeds available inventory stock.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class OutOfStockException extends RuntimeException {

    public OutOfStockException(String message) {
        super(message);
    }
}

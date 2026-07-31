package com.inventory.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Root Controller providing API welcome status and endpoint catalogue.
 */
@RestController
public class RootController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> rootWelcome() {
        return getWelcomePayload();
    }

    @GetMapping("/api/v1")
    public ResponseEntity<Map<String, Object>> apiWelcome() {
        return getWelcomePayload();
    }

    private ResponseEntity<Map<String, Object>> getWelcomePayload() {
        Map<String, Object> response = new HashMap<>();
        response.put("system", "Smart Inventory & Order Management System");
        response.put("status", "ONLINE");
        response.put("version", "1.0.0");
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("Categories", "http://localhost:8080/api/v1/categories");
        endpoints.put("Suppliers", "http://localhost:8080/api/v1/suppliers");
        endpoints.put("Products", "http://localhost:8080/api/v1/products");
        endpoints.put("Customers", "http://localhost:8080/api/v1/customers");
        endpoints.put("Orders", "http://localhost:8080/api/v1/orders");
        
        response.put("endpoints", endpoints);
        return ResponseEntity.ok(response);
    }
}

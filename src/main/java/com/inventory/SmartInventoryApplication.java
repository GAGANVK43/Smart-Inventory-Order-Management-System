package com.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Entry Point for Smart Inventory & Order Management System.
 * 
 * Concept Explanations:
 * - @SpringBootApplication: A convenience annotation that combines:
 *   1. @Configuration: Tags the class as a source of bean definitions for the application context.
 *   2. @EnableAutoConfiguration: Tells Spring Boot to start adding beans based on classpath settings, 
 *      other beans, and various property settings (e.g., configuring Tomcat and JPA automatically).
 *   3. @ComponentScan: Tells Spring to look for other components, configurations, and services in 
 *      the 'com.inventory' package and sub-packages.
 */
@SpringBootApplication
public class SmartInventoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartInventoryApplication.class, args);
        System.out.println("=================================================");
        System.out.println(" Smart Inventory & Order Management System Ready!");
        System.out.println(" Base URL: http://localhost:8080/api/v1");
        System.out.println("=================================================");
    }
}

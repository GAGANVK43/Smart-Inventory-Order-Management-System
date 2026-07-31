# Smart Inventory & Order Management System

Enterprise-grade Java backend RESTful application built with **Spring Boot 3**, **Java 21 (LTS)**, **Spring Data JPA (Hibernate)**, and **MySQL**. This application provides real-time stock management, transactional multi-item order placement, input validation, and global error handling.

---

## 🚀 Key Features

* **Category Management**: Full CRUD operations for product categorization with unique name constraints.
* **Supplier Management**: Track vendor contact information with email format and uniqueness validation.
* **Product Catalog & Inventory**:
  * Manage product inventory with real-time price and stock levels.
  * Relational mapping (`@ManyToOne`) linking products to Categories and Suppliers.
  * Search products by keyword (name or description), category, supplier, or price range.
* **Customer Management**: Register and manage customer profiles.
* **Transactional Order Processing**:
  * Create multi-item orders in a single atomic transaction (`@Transactional`).
  * Automatic total price calculation.
  * Atomic inventory stock reduction upon order placement.
  * Out-of-stock protection throwing `OutOfStockException` when requested quantity exceeds available stock.
* **Global Exception Handling**: Centralized exception management returning standardized RFC 7807 error responses.

---

## 🛠️ Technology Stack

| Component | Technology |
| :--- | :--- |
| **Language** | Java 21 (LTS) |
| **Framework** | Spring Boot 3.3.2 |
| **Web Server** | Embedded Apache Tomcat |
| **ORM / Persistence** | Spring Data JPA / Hibernate |
| **Database** | MySQL 8+ |
| **Build Tool** | Apache Maven |
| **Validation** | Jakarta Bean Validation (Hibernate Validator) |
| **IDE Compatibility** | IntelliJ IDEA Community Edition & Ultimate |

---

## 📂 Project Structure

```text
c:\Users\Admin\OneDrive\Documents\placementDrive\Projects\Smart Inventory & Order Management System
 ├── pom.xml
 ├── README.md
 └── src
     ├── main
     │   ├── java
     │   │   └── com
     │   │       └── inventory
     │   │           ├── SmartInventoryApplication.java
     │   │           ├── config/
     │   │           ├── controller/
     │   │           │    ├── CategoryController.java
     │   │           │    ├── SupplierController.java
     │   │           │    ├── ProductController.java
     │   │           │    ├── CustomerController.java
     │   │           │    └── OrderController.java
     │   │           ├── dto/
     │   │           ├── entity/
     │   │           │    ├── Category.java
     │   │           │    ├── Supplier.java
     │   │           │    ├── Product.java
     │   │           │    ├── Customer.java
     │   │           │    ├── Order.java
     │   │           │    └── OrderItem.java
     │   │           ├── exception/
     │   │           │    ├── GlobalExceptionHandler.java
     │   │           │    ├── CategoryNotFoundException.java
     │   │           │    ├── SupplierNotFoundException.java
     │   │           │    ├── ProductNotFoundException.java
     │   │           │    ├── CustomerNotFoundException.java
     │   │           │    ├── OrderNotFoundException.java
     │   │           │    └── OutOfStockException.java
     │   │           ├── repository/
     │   │           ├── service/
     │   │           └── util/
     │   └── resources
     │       └── application.properties
```

---

## 🗄️ Database Setup

1. Start your local MySQL Server on port `3306`.
2. Execute the following SQL statement in MySQL Workbench or Command Line:
   ```sql
   CREATE DATABASE IF NOT EXISTS inventory_db;
   ```
3. Update database credentials in `src/main/resources/application.properties` if needed:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/inventory_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
   spring.datasource.username=root
   spring.datasource.password=root
   ```

---

## ⚡ How to Run

### Command Line (Maven)
```bash
# Clone or navigate to project directory
cd "Smart Inventory & Order Management System"

# Compile the application
mvn clean compile

# Run the Spring Boot application
mvn spring-boot:run
```

### IntelliJ IDEA Community Edition
1. Open IntelliJ IDEA -> `File` -> `Open...` -> Select the project root folder.
2. Allow Maven to import dependencies automatically.
3. Open `src/main/java/com/inventory/SmartInventoryApplication.java`.
4. Right-click and select **Run 'SmartInventoryApplication.main()'**.

---

## 📡 REST API Endpoint Reference

Base URL: `http://localhost:8080/api/v1`

### Category Endpoints
* `POST /categories` - Create a new category
* `GET /categories` - Get all categories
* `GET /categories/{id}` - Get category by ID
* `PUT /categories/{id}` - Update category
* `DELETE /categories/{id}` - Delete category

### Supplier Endpoints
* `POST /suppliers` - Create a new supplier
* `GET /suppliers` - Get all suppliers
* `GET /suppliers/{id}` - Get supplier by ID
* `PUT /suppliers/{id}` - Update supplier
* `DELETE /suppliers/{id}` - Delete supplier

### Product Endpoints
* `POST /products` - Add a new product
* `GET /products` - Get all products
* `GET /products/{id}` - Get product by ID
* `GET /products/search?keyword=phone` - Search products by keyword
* `GET /products/category/{categoryId}` - Filter products by category
* `GET /products/supplier/{supplierId}` - Filter products by supplier
* `GET /products/price-range?min=100&max=1000` - Filter products by price range
* `PUT /products/{id}` - Update product
* `DELETE /products/{id}` - Delete product

### Customer Endpoints
* `POST /customers` - Create a new customer
* `GET /customers` - Get all customers
* `GET /customers/{id}` - Get customer by ID
* `PUT /customers/{id}` - Update customer
* `DELETE /customers/{id}` - Delete customer

### Order Endpoints
* `POST /orders` - Create a multi-item order (Auto-calculates total & deducts stock)
* `GET /orders` - View all orders
* `GET /orders/{id}` - View order details
* `GET /orders/customer/{customerId}` - View orders for a specific customer

---

## 🔮 Future Improvements

1. **Spring Security & JWT**: Implement JWT role-based authorization (ADMIN, MANAGER, CUSTOMER).
2. **Pagination & Sorting**: Add `Pageable` parameters to `getAllProducts` and `getAllOrders`.
3. **OpenAPI / Swagger Integration**: Generate interactive API documentation via `springdoc-openapi`.

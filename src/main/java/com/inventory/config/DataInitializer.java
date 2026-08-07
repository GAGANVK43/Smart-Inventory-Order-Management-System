package com.inventory.config;

import com.inventory.entity.Category;
import com.inventory.entity.Product;
import com.inventory.entity.Role;
import com.inventory.entity.Supplier;
import com.inventory.entity.User;
import com.inventory.enums.RoleName;
import com.inventory.repository.CategoryRepository;
import com.inventory.repository.ProductRepository;
import com.inventory.repository.RoleRepository;
import com.inventory.repository.SupplierRepository;
import com.inventory.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * DataInitializer runs on startup to seed essential roles, initial admin account,
 * and sample product data into MySQL if the database is empty.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;
    private final org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    public DataInitializer(RoleRepository roleRepository,
                           UserRepository userRepository,
                           CategoryRepository categoryRepository,
                           SupplierRepository supplierRepository,
                           ProductRepository productRepository,
                           PasswordEncoder passwordEncoder,
                           org.springframework.jdbc.core.JdbcTemplate jdbcTemplate) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.supplierRepository = supplierRepository;
        this.productRepository = productRepository;
        this.passwordEncoder = passwordEncoder;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        // Ensure missing timestamp columns exist in MySQL tables if schema update was skipped
        String[] alterStatements = {
            "ALTER TABLE products ADD COLUMN created_at DATETIME(6) NULL",
            "ALTER TABLE products ADD COLUMN updated_at DATETIME(6) NULL",
            "ALTER TABLE suppliers ADD COLUMN created_at DATETIME(6) NULL",
            "ALTER TABLE customers ADD COLUMN created_at DATETIME(6) NULL",
            "ALTER TABLE users ADD COLUMN created_at DATETIME(6) NULL",
            "ALTER TABLE inventory_movements ADD COLUMN created_at DATETIME(6) NULL"
        };
        for (String sql : alterStatements) {
            try {
                jdbcTemplate.execute(sql);
            } catch (Exception e) {
                // Column already exists or table not created yet
            }
        }
        // 1. Seed Roles
        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                .orElseGet(() -> roleRepository.save(new Role(RoleName.ROLE_ADMIN)));

        Role managerRole = roleRepository.findByName(RoleName.ROLE_MANAGER)
                .orElseGet(() -> roleRepository.save(new Role(RoleName.ROLE_MANAGER)));

        Role staffRole = roleRepository.findByName(RoleName.ROLE_STAFF)
                .orElseGet(() -> roleRepository.save(new Role(RoleName.ROLE_STAFF)));

        // 2. Seed Default Admin User
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@inventory.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEnabled(true);

            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            roles.add(managerRole);
            roles.add(staffRole);
            admin.setRoles(roles);

            userRepository.save(admin);
            System.out.println(">>> Seeded default admin user: admin / admin123");
        }

        // 3. Seed Sample Category & Supplier & Product if empty
        if (categoryRepository.count() == 0) {
            Category electronics = new Category();
            electronics.setName("Electronics");
            electronics.setDescription("Electronic components, gadgets, and accessories");
            electronics = categoryRepository.save(electronics);

            Supplier techSupplier = new Supplier();
            techSupplier.setName("Tech Corp Solutions");
            techSupplier.setContactPerson("John Doe");
            techSupplier.setEmail("john@techcorp.com");
            techSupplier.setPhone("+1-555-0199");
            techSupplier.setAddress("123 Tech Blvd, Silicon Valley, CA");
            techSupplier.setActive(true);
            techSupplier = supplierRepository.save(techSupplier);

            Product prod = new Product();
            prod.setName("Wireless Ergonomic Mouse");
            prod.setSku("SKU-ELE-PRD-0001");
            prod.setBarcode("8901234567890");
            prod.setDescription("2.4GHz High Precision Wireless Mouse");
            prod.setPrice(new BigDecimal("49.99"));
            prod.setQuantity(150);
            prod.setReorderLevel(20);
            prod.setCategory(electronics);
            prod.setSupplier(techSupplier);
            prod.setActive(true);
            productRepository.save(prod);

            System.out.println(">>> Seeded initial category, supplier, and product.");
        }
    }
}

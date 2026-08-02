package com.inventory.service.impl;

import com.inventory.dto.response.DashboardStatsDto;
import com.inventory.dto.response.RevenueTrendDto;
import com.inventory.dto.response.StockHealthDto;
import com.inventory.dto.response.TopProductDto;
import com.inventory.entity.Category;
import com.inventory.entity.Order;
import com.inventory.entity.OrderItem;
import com.inventory.entity.Product;
import com.inventory.enums.OrderStatus;
import com.inventory.repository.CategoryRepository;
import com.inventory.repository.CustomerRepository;
import com.inventory.repository.OrderRepository;
import com.inventory.repository.ProductRepository;
import com.inventory.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public AnalyticsServiceImpl(OrderRepository orderRepository,
                                 ProductRepository productRepository,
                                 CustomerRepository customerRepository,
                                 CategoryRepository categoryRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsDto getDashboardStats() {
        List<Order> allOrders = orderRepository.findAll();
        List<Product> allProducts = productRepository.findAll();

        BigDecimal totalRevenue = allOrders.stream()
                .filter(o -> o.getStatus() != OrderStatus.CANCELLED)
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalOrders = allOrders.stream()
                .filter(o -> o.getStatus() != OrderStatus.CANCELLED)
                .count();

        long totalProducts = allProducts.stream()
                .filter(Product::isActive)
                .count();

        long totalCustomers = customerRepository.count();

        long lowStockCount = allProducts.stream()
                .filter(p -> p.isActive() && p.getQuantity() <= p.getReorderLevel())
                .count();

        return new DashboardStatsDto(totalRevenue, totalOrders, totalProducts, totalCustomers, lowStockCount);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopProductDto> getTopSellingProducts(int limit) {
        List<Order> validOrders = orderRepository.findAll().stream()
                .filter(o -> o.getStatus() != OrderStatus.CANCELLED)
                .collect(Collectors.toList());

        Map<Product, Long> productUnitsMap = new HashMap<>();
        Map<Product, BigDecimal> productRevenueMap = new HashMap<>();

        for (Order order : validOrders) {
            for (OrderItem item : order.getOrderItems()) {
                Product p = item.getProduct();
                long qty = item.getQuantity();
                BigDecimal itemRev = item.getPrice().multiply(BigDecimal.valueOf(qty));

                productUnitsMap.put(p, productUnitsMap.getOrDefault(p, 0L) + qty);
                productRevenueMap.put(p, productRevenueMap.getOrDefault(p, BigDecimal.ZERO).add(itemRev));
            }
        }

        return productUnitsMap.entrySet().stream()
                .map(entry -> {
                    Product p = entry.getKey();
                    long units = entry.getValue();
                    BigDecimal rev = productRevenueMap.getOrDefault(p, BigDecimal.ZERO);
                    return new TopProductDto(p.getId(), p.getName(), p.getSku(), p.getCategory().getName(), units, rev);
                })
                .sorted(Comparator.comparing(TopProductDto::getTotalRevenueGenerated).reversed())
                .limit(limit > 0 ? limit : 5)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RevenueTrendDto> getRevenueTrends() {
        List<Order> validOrders = orderRepository.findAll().stream()
                .filter(o -> o.getStatus() != OrderStatus.CANCELLED)
                .collect(Collectors.toList());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Map<String, BigDecimal> dateRevenueMap = new TreeMap<>();
        Map<String, Long> dateCountMap = new TreeMap<>();

        for (Order order : validOrders) {
            String dateStr = order.getOrderDate().format(formatter);
            dateRevenueMap.put(dateStr, dateRevenueMap.getOrDefault(dateStr, BigDecimal.ZERO).add(order.getTotalAmount()));
            dateCountMap.put(dateStr, dateCountMap.getOrDefault(dateStr, 0L) + 1);
        }

        List<RevenueTrendDto> trends = new ArrayList<>();
        for (String dateStr : dateRevenueMap.keySet()) {
            trends.add(new RevenueTrendDto(dateStr, dateRevenueMap.get(dateStr), dateCountMap.get(dateStr)));
        }

        return trends;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockHealthDto> getStockHealthSummary() {
        List<Category> categories = categoryRepository.findAll();
        List<Product> products = productRepository.findAll();

        List<StockHealthDto> healthList = new ArrayList<>();

        for (Category category : categories) {
            List<Product> catProducts = products.stream()
                    .filter(p -> p.isActive() && p.getCategory().getId().equals(category.getId()))
                    .collect(Collectors.toList());

            long outOfStock = catProducts.stream().filter(p -> p.getQuantity() == 0).count();
            long lowStock = catProducts.stream().filter(p -> p.getQuantity() > 0 && p.getQuantity() <= p.getReorderLevel()).count();
            long inStock = catProducts.stream().filter(p -> p.getQuantity() > p.getReorderLevel()).count();

            healthList.add(new StockHealthDto(category.getName(), inStock, lowStock, outOfStock));
        }

        return healthList;
    }
}

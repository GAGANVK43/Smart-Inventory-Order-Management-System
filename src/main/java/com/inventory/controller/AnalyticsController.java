package com.inventory.controller;

import com.inventory.dto.response.*;
import com.inventory.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/analytics")
@CrossOrigin
@Tag(name = "Executive Analytics & Business Intelligence", description = "Endpoints for real-time dashboard KPIs, revenue trend reports, top-selling products rank, and category stock health analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @Autowired
    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN')")
    @Operation(summary = "Get Dashboard KPI Summary", description = "Retrieve total revenue, order count, active products count, total customers, and low stock alert count")
    public ResponseEntity<ApiResponse<DashboardStatsDto>> getDashboardStats() {
        DashboardStatsDto stats = analyticsService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/top-selling")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN')")
    @Operation(summary = "Get Top-Selling Products Report", description = "Retrieve top-performing products ranked by total revenue generated and units sold")
    public ResponseEntity<ApiResponse<List<TopProductDto>>> getTopSellingProducts(@RequestParam(defaultValue = "5") int limit) {
        List<TopProductDto> topProducts = analyticsService.getTopSellingProducts(limit);
        return ResponseEntity.ok(ApiResponse.success(topProducts));
    }

    @GetMapping("/revenue-trends")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @Operation(summary = "Get Revenue Trend Analytics", description = "Retrieve revenue and order volume aggregations grouped by date periods for charts")
    public ResponseEntity<ApiResponse<List<RevenueTrendDto>>> getRevenueTrends() {
        List<RevenueTrendDto> trends = analyticsService.getRevenueTrends();
        return ResponseEntity.ok(ApiResponse.success(trends));
    }

    @GetMapping("/stock-health")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN')")
    @Operation(summary = "Get Stock Health Distribution", description = "Retrieve category-wise in-stock, low-stock, and out-of-stock product distributions")
    public ResponseEntity<ApiResponse<List<StockHealthDto>>> getStockHealthSummary() {
        List<StockHealthDto> stockHealth = analyticsService.getStockHealthSummary();
        return ResponseEntity.ok(ApiResponse.success(stockHealth));
    }
}

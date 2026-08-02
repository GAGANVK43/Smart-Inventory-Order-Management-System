package com.inventory.service;

import com.inventory.dto.response.DashboardStatsDto;
import com.inventory.dto.response.RevenueTrendDto;
import com.inventory.dto.response.StockHealthDto;
import com.inventory.dto.response.TopProductDto;

import java.util.List;

public interface AnalyticsService {
    DashboardStatsDto getDashboardStats();
    List<TopProductDto> getTopSellingProducts(int limit);
    List<RevenueTrendDto> getRevenueTrends();
    List<StockHealthDto> getStockHealthSummary();
}

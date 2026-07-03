package com.example.crm.dto;

public record CustomerStatsResponse(
        long totalCustomers,
        long newCustomersThisMonth,
        long vipCustomers,
        double churnRate
) {
}

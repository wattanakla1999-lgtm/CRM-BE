package com.example.crm.dto;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

import com.example.crm.entity.Customer;
import com.example.crm.repository.CustomerSummaryProjection;

public record CustomerResponse(
        Long id,
        String name,
        String email,
        String phone,
        String companyName,
        String statusName,
        List<String> tags,
        OffsetDateTime createdAt
) {
    private static final ZoneId BANGKOK_ZONE = ZoneId.of("Asia/Bangkok");

    public static CustomerResponse from(Customer customer) {
        return from(customer, List.of());
    }

    public static CustomerResponse from(Customer customer, List<String> tags) {
        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getCompany() != null ? customer.getCompany().getName() : null,
                customer.getStatus() != null ? customer.getStatus().getName() : null,
                tags,
                customer.getCreatedAt().atZone(BANGKOK_ZONE).toOffsetDateTime()
        );
    }

    public static CustomerResponse from(CustomerSummaryProjection projection) {
        return new CustomerResponse(
                projection.getId(),
                projection.getName(),
                projection.getEmail(),
                projection.getPhone(),
                projection.getCompanyName(),
                projection.getStatusName(),
                splitTags(projection.getTags()),
                projection.getCreatedAt() != null
                        ? projection.getCreatedAt().atZone(BANGKOK_ZONE).toOffsetDateTime()
                        : null
        );
    }

    private static List<String> splitTags(String tags) {
        if (tags == null || tags.isBlank()) {
            return List.of();
        }

        return Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(tag -> !tag.isEmpty())
                .toList();
    }
}

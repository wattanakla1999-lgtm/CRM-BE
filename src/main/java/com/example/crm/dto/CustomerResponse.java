package com.example.crm.dto;

import com.example.crm.entity.Customer;
import java.time.OffsetDateTime;
import java.time.ZoneId;

public record CustomerResponse(
        Long id,
        String name,
        String email,
        String phone,
        OffsetDateTime createdAt
) {
    private static final ZoneId BANGKOK_ZONE = ZoneId.of("Asia/Bangkok");

    public static CustomerResponse from(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getCreatedAt().atZone(BANGKOK_ZONE).toOffsetDateTime()
        );
    }
}

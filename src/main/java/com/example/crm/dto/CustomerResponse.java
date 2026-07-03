package com.example.crm.dto;

import com.example.crm.entity.Customer;
import java.time.Instant;

public record CustomerResponse(
        Long id,
        String name,
        String email,
        String phone,
        Instant createdAt
) {
    public static CustomerResponse from(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getCreatedAt()
        );
    }
}

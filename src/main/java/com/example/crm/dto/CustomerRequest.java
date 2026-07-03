package com.example.crm.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomerRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 160, message = "Name must be at most 160 characters")
        String name,

        @Email(message = "Email must be valid")
        @Size(max = 160, message = "Email must be at most 160 characters")
        String email,

        @Size(max = 40, message = "Phone must be at most 40 characters")
        String phone
) {
}

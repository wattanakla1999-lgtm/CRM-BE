package com.example.crm.dto;

import java.time.OffsetDateTime;
import java.time.ZoneId;

public record ApiResponse<T>(
        boolean success,
        int status,
        String message,
        T data,
        OffsetDateTime timestamp
) {
    private static final ZoneId BANGKOK_ZONE = ZoneId.of("Asia/Bangkok");

    public static <T> ApiResponse<T> success(int status, String message, T data) {
        return new ApiResponse<>(true, status, message, data, OffsetDateTime.now(BANGKOK_ZONE));
    }

    public static <T> ApiResponse<T> error(int status, String message, T data) {
        return new ApiResponse<>(false, status, message, data, OffsetDateTime.now(BANGKOK_ZONE));
    }
}

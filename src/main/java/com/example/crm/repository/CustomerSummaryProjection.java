package com.example.crm.repository;

import java.time.Instant;

public interface CustomerSummaryProjection {
    Long getId();

    String getName();

    String getEmail();

    String getPhone();

    String getCompanyName();

    String getStatusName();

    String getTags();

    Instant getCreatedAt();
}

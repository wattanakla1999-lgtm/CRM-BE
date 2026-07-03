package com.example.crm.controller;

import com.example.crm.dto.ApiResponse;
import java.time.Instant;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> health() {
        Map<String, Object> data = Map.of(
                "status", "UP",
                "service", "crm-backend",
                "timestamp", Instant.now().toString()
        );
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                "Service is healthy",
                data
        ));
    }
}

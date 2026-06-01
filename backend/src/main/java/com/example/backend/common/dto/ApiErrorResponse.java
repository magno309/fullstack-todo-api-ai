package com.example.backend.common.dto;

import java.time.Instant;
import java.util.Map;

public record ApiErrorResponse(
    String message,
    int status,
    Instant timestamp,
    Map<String, String> validationDetails
) {
    public static ApiErrorResponse of(String message, int status, Map<String, String> validationDetails) {
        return new ApiErrorResponse(message, status, Instant.now(), validationDetails);
    }
}

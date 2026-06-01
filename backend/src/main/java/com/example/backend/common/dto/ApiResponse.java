package com.example.backend.common.dto;

import java.time.Instant;

public record ApiResponse<T>(
    String message,
    int status,
    Instant timestamp,
    T data
) {
    public static <T> ApiResponse<T> of(String message, int status, T data) {
        return new ApiResponse<>(message, status, Instant.now(), data);
    }
}

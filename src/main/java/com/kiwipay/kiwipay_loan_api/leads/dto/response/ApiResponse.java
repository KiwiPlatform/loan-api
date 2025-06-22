package com.kiwipay.kiwipay_loan_api.leads.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    private boolean ok;
    private T data;
    private String errorType;
    private String message;
    private Integer status;
    private Map<String, String> details;
    private LocalDateTime timestamp;
    private String path;
    
    // Factory methods para crear respuestas
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .ok(true)
                .data(data)
                .status(200)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static <T> ApiResponse<T> created(T data) {
        return ApiResponse.<T>builder()
                .ok(true)
                .data(data)
                .status(201)
                .message("Recurso creado exitosamente")
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static <T> ApiResponse<T> error(String errorType, String message, int status) {
        return ApiResponse.<T>builder()
                .ok(false)
                .errorType(errorType)
                .message(message)
                .status(status)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static <T> ApiResponse<T> error(String errorType, String message, int status, Map<String, String> details) {
        return ApiResponse.<T>builder()
                .ok(false)
                .errorType(errorType)
                .message(message)
                .status(status)
                .details(details)
                .timestamp(LocalDateTime.now())
                .build();
    }
} 
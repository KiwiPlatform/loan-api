package com.kiwipay.kiwipay_loan_api.leads.exception;

import com.kiwipay.kiwipay_loan_api.leads.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        log.warn("Validación fallida: {}", errors);
        
        ApiResponse<Void> response = ApiResponse.error(
                "VALIDATION_ERROR",
                "Error de validación en los datos enviados",
                HttpStatus.BAD_REQUEST.value(),
                errors
        );
        response.setPath(request.getRequestURI());
        
        return ResponseEntity.badRequest().body(response);
    }
    
    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ApiResponse<Void>> handleExternalServiceException(
            ExternalServiceException ex,
            HttpServletRequest request
    ) {
        log.error("Error del servicio externo: {}", ex.getMessage());
        
        ApiResponse<Void> response = ApiResponse.error(
                "EXTERNAL_SERVICE_ERROR",
                ex.getMessage(),
                ex.getStatusCode().value()
        );
        response.setPath(request.getRequestURI());
        
        return ResponseEntity.status(ex.getStatusCode()).body(response);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(
            Exception ex,
            HttpServletRequest request
    ) {
        log.error("Error no manejado: ", ex);
        
        ApiResponse<Void> response = ApiResponse.error(
                "INTERNAL_ERROR",
                "Error interno del servidor",
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        response.setPath(request.getRequestURI());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
} 
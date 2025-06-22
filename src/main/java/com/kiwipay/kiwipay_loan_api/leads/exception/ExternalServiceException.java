package com.kiwipay.kiwipay_loan_api.leads.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class ExternalServiceException extends RuntimeException {
    
    private final HttpStatusCode statusCode;
    
    public ExternalServiceException(String message, HttpStatusCode statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
    
    public ExternalServiceException(String message, HttpStatusCode statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }
} 
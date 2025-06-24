package com.kiwipay.kiwipay_loan_api.leads.service;

import com.kiwipay.kiwipay_loan_api.leads.client.BackendClient;
import com.kiwipay.kiwipay_loan_api.leads.dto.request.SquarespaceBackendRequest;
import com.kiwipay.kiwipay_loan_api.leads.dto.request.SquarespaceLeadRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SquarespaceServiceImpl implements SquarespaceService {
    
    private final BackendClient backendClient;
    
    @Override
    public Mono<Map<String, Object>> processSquarespaceLead(SquarespaceLeadRequest request) {
        log.info("Procesando lead de Squarespace para enviar al backend principal");
        
        // Mapeo con conversión de montos a números
        SquarespaceBackendRequest backendRequest = SquarespaceBackendRequest.builder()
                .receptionistName(request.getReceptionistName())
                .sede(request.getSede())
                .clientName(request.getClientName())
                .dni(request.getDni())
                .monthlyIncome(parseMoneyAmount(request.getMonthlyIncome()))
                .treatmentCost(parseMoneyAmount(request.getTreatmentCost()))
                .phone(request.getPhone())
                .source("squarespace") // Identificar origen
                .build();
        
        log.info("Enviando al backend principal: {}", backendRequest);
        
        return backendClient.createSquarespaceLead(backendRequest)
                .map(response -> {
                    log.info("✅ Lead enviado exitosamente al backend. ID: {}", response.getId());
                    return createSuccessResponse(response);
                })
                .onErrorResume(error -> {
                    log.error("❌ Error enviando al backend principal: {}", error.getMessage(), error);
                    return Mono.just(createErrorResponse(error.getMessage()));
                });
    }
    
    private BigDecimal parseMoneyAmount(String amount) {
        if (amount == null || amount.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        try {
            // Limpiar el string: remover S/., espacios, comas
            String cleanAmount = amount.trim()
                    .replace("S/.", "")
                    .replace("S/", "")
                    .replace(",", "")
                    .replace(" ", "");
            
            return new BigDecimal(cleanAmount);
        } catch (NumberFormatException e) {
            log.warn("No se pudo parsear el monto '{}', usando 0", amount);
            return BigDecimal.ZERO;
        }
    }
    
    private Map<String, Object> createSuccessResponse(Object backendResponse) {
        Map<String, Object> response = new HashMap<>();
        response.put("ok", true);
        response.put("message", "Lead enviado exitosamente al backend principal");
        response.put("timestamp", LocalDateTime.now());
        response.put("backendResponse", backendResponse);
        return response;
    }
    
    private Map<String, Object> createErrorResponse(String errorMessage) {
        Map<String, Object> response = new HashMap<>();
        response.put("ok", false);
        response.put("error", "Error al enviar al backend principal: " + errorMessage);
        response.put("timestamp", LocalDateTime.now());
        return response;
    }
} 
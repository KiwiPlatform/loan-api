package com.kiwipay.kiwipay_loan_api.leads.service;

import com.kiwipay.kiwipay_loan_api.leads.client.BackendClient;
import com.kiwipay.kiwipay_loan_api.leads.dto.request.BackendLeadRequest;
import com.kiwipay.kiwipay_loan_api.leads.dto.request.LeadRequestDto;
import com.kiwipay.kiwipay_loan_api.leads.dto.response.ApiResponse;
import com.kiwipay.kiwipay_loan_api.leads.dto.response.LeadResponseDto;
import com.kiwipay.kiwipay_loan_api.leads.mapper.LeadMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeadServiceImpl implements LeadService {
    
    private final BackendClient backendClient;
    private final LeadMapper leadMapper;
    
    @Override
    public Mono<ApiResponse<LeadResponseDto>> processLead(LeadRequestDto request) {
        log.info("Procesando nuevo lead: DNI={}, Cliente={}", request.getDni(), request.getClientName());
        
        // Validaciones adicionales de negocio si son necesarias
        return validateBusinessRules(request)
                .then(Mono.defer(() -> {
                    // Mapear DTO del frontend al formato del backend
                    BackendLeadRequest backendRequest = leadMapper.toBackendRequest(request);
                    
                    // Enviar al backend principal
                    return backendClient.createLead(backendRequest);
                }))
                .map(response -> {
                    log.info("Lead procesado exitosamente: ID={}", response.getId());
                    return ApiResponse.created(response);
                })
                .onErrorResume(error -> {
                    log.error("Error procesando lead: {}", error.getMessage(), error);
                    return Mono.just(handleError(error));
                });
    }
    
    private Mono<Void> validateBusinessRules(LeadRequestDto request) {
        // Aquí puedes agregar validaciones adicionales de negocio
        // Por ejemplo: verificar horarios de atención, límites diarios, etc.
        
        // Por ahora, solo validamos que el costo del tratamiento no exceda cierto múltiplo del ingreso
        if (request.getTreatmentCost().compareTo(request.getMonthlyIncome().multiply(new java.math.BigDecimal("12"))) > 0) {
            log.warn("Costo del tratamiento excede el ingreso anual del cliente");
            // Podrías rechazar o marcar el lead de alguna manera especial
        }
        
        return Mono.empty();
    }
    
    private ApiResponse<LeadResponseDto> handleError(Throwable error) {
        if (error instanceof com.kiwipay.kiwipay_loan_api.leads.exception.ExternalServiceException ese) {
            return ApiResponse.error(
                    "EXTERNAL_SERVICE_ERROR",
                    ese.getMessage(),
                    ese.getStatusCode().value()
            );
        }
        
        return ApiResponse.error(
                "INTERNAL_ERROR",
                "Error interno del servidor. Por favor, intente más tarde.",
                500
        );
    }
} 
package com.kiwipay.kiwipay_loan_api.leads.client;

import com.kiwipay.kiwipay_loan_api.leads.dto.request.BackendLeadRequest;
import com.kiwipay.kiwipay_loan_api.leads.dto.request.SquarespaceBackendRequest;
import com.kiwipay.kiwipay_loan_api.leads.dto.response.LeadResponseDto;
import com.kiwipay.kiwipay_loan_api.leads.exception.ExternalServiceException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BackendClientImpl implements BackendClient {
    
    private final WebClient backendWebClient;
    
    @Value("${kiwipay.backend.endpoints.create-lead}")
    private String createLeadEndpoint;
    
    @Value("${kiwipay.backend.endpoints.create-squarespace-lead:/leads/squarespace}")
    private String createSquarespaceLeadEndpoint;
    
    @Value("${kiwipay.backend.endpoints.get-clinics}")
    private String getClinicsEndpoint;
    
    @Value("${kiwipay.backend.endpoints.get-medical-specialties}")
    private String getMedicalSpecialtiesEndpoint;
    
    @Override
    @CircuitBreaker(name = "backend", fallbackMethod = "createLeadFallback")
    @Retry(name = "backend")
    public Mono<LeadResponseDto> createLead(BackendLeadRequest request) {
        log.info("Enviando lead al backend principal: DNI={}", request.getDni());
        
        return backendWebClient
                .post()
                .uri(createLeadEndpoint)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(LeadResponseDto.class)
                .doOnSuccess(response -> log.info("Lead creado exitosamente: ID={}", response.getId()))
                .doOnError(error -> log.error("Error creando lead: {}", error.getMessage()))
                .onErrorMap(this::handleWebClientError);
    }
    
    @Override
    @CircuitBreaker(name = "backend", fallbackMethod = "createSquarespaceLeadFallback")
    @Retry(name = "backend")
    public Mono<LeadResponseDto> createSquarespaceLead(SquarespaceBackendRequest request) {
        log.info("Enviando lead de Squarespace al backend principal: DNI={}, Cliente={}", 
                request.getDni(), request.getClientName());
        
        return backendWebClient
                .post()
                .uri(createSquarespaceLeadEndpoint) // Endpoint específico para Squarespace
                .bodyValue(request)
                .retrieve()
                .bodyToMono(LeadResponseDto.class)
                .doOnSuccess(response -> log.info("Lead de Squarespace creado exitosamente: ID={}", response.getId()))
                .doOnError(error -> log.error("Error creando lead de Squarespace: {}", error.getMessage()))
                .onErrorMap(this::handleWebClientError);
    }
    
    @Override
    @CircuitBreaker(name = "backend", fallbackMethod = "getClinicsFallback")
    @Retry(name = "backend")
    public Mono<List<ClinicDto>> getClinics() {
        log.debug("Obteniendo lista de clínicas del backend");
        
        return backendWebClient
                .get()
                .uri(getClinicsEndpoint)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ClinicDto>>() {})
                .doOnSuccess(clinics -> log.debug("Obtenidas {} clínicas", clinics.size()))
                .onErrorMap(this::handleWebClientError);
    }
    
    @Override
    @CircuitBreaker(name = "backend", fallbackMethod = "getMedicalSpecialtiesFallback")
    @Retry(name = "backend")
    public Mono<List<MedicalSpecialtyDto>> getMedicalSpecialties() {
        log.debug("Obteniendo lista de especialidades médicas del backend");
        
        return backendWebClient
                .get()
                .uri(getMedicalSpecialtiesEndpoint)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<MedicalSpecialtyDto>>() {})
                .doOnSuccess(specialties -> log.debug("Obtenidas {} especialidades", specialties.size()))
                .onErrorMap(this::handleWebClientError);
    }
    
    // Métodos de fallback para Circuit Breaker
    private Mono<LeadResponseDto> createLeadFallback(BackendLeadRequest request, Exception ex) {
        log.error("Circuit breaker activado para createLead. Error: {}", ex.getMessage());
        return Mono.error(new ExternalServiceException(
                "El servicio principal no está disponible temporalmente. Por favor, intente más tarde.",
                HttpStatus.SERVICE_UNAVAILABLE
        ));
    }
    
    private Mono<LeadResponseDto> createSquarespaceLeadFallback(SquarespaceBackendRequest request, Exception ex) {
        log.error("Circuit breaker activado para createSquarespaceLead. Error: {}", ex.getMessage());
        return Mono.error(new ExternalServiceException(
                "El servicio principal no está disponible temporalmente. Por favor, intente más tarde.",
                HttpStatus.SERVICE_UNAVAILABLE
        ));
    }
    
    private Mono<List<ClinicDto>> getClinicsFallback(Exception ex) {
        log.error("Circuit breaker activado para getClinics. Error: {}", ex.getMessage());
        return Mono.error(new ExternalServiceException(
                "No se pueden obtener las clínicas en este momento",
                HttpStatus.SERVICE_UNAVAILABLE
        ));
    }
    
    private Mono<List<MedicalSpecialtyDto>> getMedicalSpecialtiesFallback(Exception ex) {
        log.error("Circuit breaker activado para getMedicalSpecialties. Error: {}", ex.getMessage());
        return Mono.error(new ExternalServiceException(
                "No se pueden obtener las especialidades médicas en este momento",
                HttpStatus.SERVICE_UNAVAILABLE
        ));
    }
    
    // Manejo de errores del WebClient
    private Throwable handleWebClientError(Throwable error) {
        if (error instanceof WebClientResponseException wcre) {
            log.error("Error del backend: Status={}, Body={}", 
                    wcre.getStatusCode(), wcre.getResponseBodyAsString());
            
            return new ExternalServiceException(
                    "Error del servicio principal: " + wcre.getMessage(),
                    wcre.getStatusCode()
            );
        }
        
        log.error("Error inesperado comunicándose con el backend", error);
        return new ExternalServiceException(
                "Error comunicándose con el servicio principal",
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
} 
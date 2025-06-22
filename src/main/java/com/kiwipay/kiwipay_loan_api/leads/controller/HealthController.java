package com.kiwipay.kiwipay_loan_api.leads.controller;

import com.kiwipay.kiwipay_loan_api.leads.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/health")
@RequiredArgsConstructor
@Tag(name = "Health", description = "Health check endpoints")
public class HealthController {
    
    private final WebClient backendWebClient;
    
    @Value("${spring.application.name}")
    private String appName;
    
    @Value("${kiwipay.backend.base-url}")
    private String backendUrl;
    
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Health check", description = "Verifica el estado del middleware y del backend principal")
    public Mono<ResponseEntity<ApiResponse<Map<String, Object>>>> healthCheck() {
        log.debug("Ejecutando health check");
        
        Map<String, Object> healthData = new HashMap<>();
        healthData.put("service", appName);
        healthData.put("status", "UP");
        healthData.put("timestamp", LocalDateTime.now());
        
        // Verificar conectividad con el backend
        return checkBackendHealth()
                .map(backendStatus -> {
                    healthData.put("backend", Map.of(
                            "url", backendUrl,
                            "status", backendStatus ? "UP" : "DOWN",
                            "available", backendStatus
                    ));
                    return ResponseEntity.ok(ApiResponse.success(healthData));
                })
                .onErrorResume(error -> {
                    log.error("Error en health check: {}", error.getMessage());
                    healthData.put("backend", Map.of(
                            "url", backendUrl,
                            "status", "DOWN",
                            "available", false,
                            "error", error.getMessage()
                    ));
                    return Mono.just(ResponseEntity.ok(ApiResponse.success(healthData)));
                });
    }
    
    private Mono<Boolean> checkBackendHealth() {
        return backendWebClient
                .get()
                .uri("/actuator/health")
                .retrieve()
                .toBodilessEntity()
                .map(response -> response.getStatusCode().is2xxSuccessful())
                .onErrorReturn(false);
    }
} 
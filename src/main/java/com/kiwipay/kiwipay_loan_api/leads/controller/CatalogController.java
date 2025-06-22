package com.kiwipay.kiwipay_loan_api.leads.controller;

import com.kiwipay.kiwipay_loan_api.leads.client.BackendClient;
import com.kiwipay.kiwipay_loan_api.leads.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Catálogos", description = "Obtener catálogos del sistema")
@CrossOrigin(originPatterns = "*")
public class CatalogController {
    
    private final BackendClient backendClient;
    
    @GetMapping(value = "/clinics", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtener clínicas", description = "Lista de clínicas activas")
    public Mono<ResponseEntity<ApiResponse<List<BackendClient.ClinicDto>>>> getClinics() {
        log.debug("Solicitando lista de clínicas");
        
        return backendClient.getClinics()
                .map(clinics -> {
                    log.debug("Retornando {} clínicas", clinics.size());
                    return ResponseEntity.ok(ApiResponse.success(clinics));
                })
                .onErrorResume(error -> {
                    log.error("Error obteniendo clínicas: {}", error.getMessage());
                    return Mono.just(ResponseEntity
                            .status(503)
                            .body(ApiResponse.error(
                                    "SERVICE_UNAVAILABLE",
                                    "No se pueden obtener las clínicas en este momento",
                                    503
                            ))
                    );
                });
    }
    
    @GetMapping(value = "/medical-specialties", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtener especialidades médicas", description = "Lista de especialidades activas")
    public Mono<ResponseEntity<ApiResponse<List<BackendClient.MedicalSpecialtyDto>>>> getMedicalSpecialties() {
        log.debug("Solicitando lista de especialidades médicas");
        
        return backendClient.getMedicalSpecialties()
                .map(specialties -> {
                    log.debug("Retornando {} especialidades", specialties.size());
                    return ResponseEntity.ok(ApiResponse.success(specialties));
                })
                .onErrorResume(error -> {
                    log.error("Error obteniendo especialidades: {}", error.getMessage());
                    return Mono.just(ResponseEntity
                            .status(503)
                            .body(ApiResponse.error(
                                    "SERVICE_UNAVAILABLE",
                                    "No se pueden obtener las especialidades médicas en este momento",
                                    503
                            ))
                    );
                });
    }
} 
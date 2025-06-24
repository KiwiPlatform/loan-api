package com.kiwipay.kiwipay_loan_api.leads.controller;

import com.kiwipay.kiwipay_loan_api.leads.dto.request.SquarespaceLeadRequest;
import com.kiwipay.kiwipay_loan_api.leads.service.SquarespaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/squarespace")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*")
@Tag(name = "Squarespace", description = "Endpoint específico para formulario de Squarespace")
public class SquarespaceController {
    
    private final SquarespaceService squarespaceService;
    
    @PostMapping(value = "/lead", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Crear lead desde Squarespace", description = "Recibe los campos del formulario de Squarespace y los envía al backend principal")
    public Mono<ResponseEntity<Map<String, Object>>> createSquarespaceLead(
            @RequestBody SquarespaceLeadRequest request
    ) {
        log.info("=== RECIBIENDO LEAD DESDE SQUARESPACE ===");
        log.info("Recepcionista: {}", request.getReceptionistName());
        log.info("Sede: {}", request.getSede());
        log.info("Cliente: {}", request.getClientName());
        log.info("DNI: {}", request.getDni());
        log.info("Ingreso Mensual: {}", request.getMonthlyIncome());
        log.info("Costo Tratamiento: {}", request.getTreatmentCost());
        log.info("Teléfono: {}", request.getPhone());
        
        // Validación básica - solo campos obligatorios según la imagen
        if (isNullOrEmpty(request.getDni()) || 
            isNullOrEmpty(request.getMonthlyIncome()) || 
            isNullOrEmpty(request.getTreatmentCost()) || 
            isNullOrEmpty(request.getPhone())) {
            
            log.warn("Faltan campos obligatorios");
            return Mono.just(ResponseEntity.badRequest().body(createErrorResponse(
                "Faltan campos obligatorios: DNI, Ingreso Mensual, Costo Tratamiento y Teléfono son requeridos"
            )));
        }
        
        // Enviar al backend principal a través del servicio
        return squarespaceService.processSquarespaceLead(request)
                .map(response -> {
                    if ((Boolean) response.get("ok")) {
                        return ResponseEntity.status(HttpStatus.CREATED).body(response);
                    } else {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                    }
                })
                .onErrorResume(error -> {
                    log.error("Error inesperado procesando lead de Squarespace", error);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(createErrorResponse("Error interno del servidor: " + error.getMessage())));
                });
    }
    
    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
    
    private Map<String, Object> createSuccessResponse(SquarespaceLeadRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("ok", true);
        response.put("message", "Lead de Squarespace recibido exitosamente");
        response.put("timestamp", LocalDateTime.now());
        response.put("data", Map.of(
            "dni", request.getDni(),
            "clientName", request.getClientName(),
            "receptionistName", request.getReceptionistName(),
            "sede", request.getSede(),
            "monthlyIncome", request.getMonthlyIncome(),
            "treatmentCost", request.getTreatmentCost(),
            "phone", request.getPhone()
        ));
        return response;
    }
    
    private Map<String, Object> createErrorResponse(String errorMessage) {
        Map<String, Object> response = new HashMap<>();
        response.put("ok", false);
        response.put("error", errorMessage);
        response.put("timestamp", LocalDateTime.now());
        return response;
    }
} 
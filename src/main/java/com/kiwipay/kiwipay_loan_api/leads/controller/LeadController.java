package com.kiwipay.kiwipay_loan_api.leads.controller;

import com.kiwipay.kiwipay_loan_api.leads.dto.request.LeadRequestDto;
import com.kiwipay.kiwipay_loan_api.leads.dto.response.ApiResponse;
import com.kiwipay.kiwipay_loan_api.leads.dto.response.LeadResponseDto;
import com.kiwipay.kiwipay_loan_api.leads.service.LeadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1/leads")
@RequiredArgsConstructor
@Tag(name = "Leads", description = "Gestión de leads de préstamos desde landings")
@CrossOrigin(originPatterns = "*") // ⭐ PERMITE CUALQUIER ORIGEN
public class LeadController {
    
    private final LeadService leadService;
    
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Crear nuevo lead de préstamo", description = "Recibe los datos del formulario desde el landing")
    public Mono<ResponseEntity<ApiResponse<LeadResponseDto>>> createLead(
            @Valid @RequestBody LeadRequestDto request
    ) {
        log.info("Recibiendo lead desde landing. Cliente: {}", request.getClientName());
        
        return leadService.processLead(request)
                .map(response -> ResponseEntity
                        .status(response.isOk() ? HttpStatus.CREATED : HttpStatus.valueOf(response.getStatus()))
                        .body(response)
                );
    }
} 
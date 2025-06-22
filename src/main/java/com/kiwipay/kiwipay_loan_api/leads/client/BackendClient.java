package com.kiwipay.kiwipay_loan_api.leads.client;

import com.kiwipay.kiwipay_loan_api.leads.dto.request.BackendLeadRequest;
import com.kiwipay.kiwipay_loan_api.leads.dto.response.LeadResponseDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface BackendClient {
    
    Mono<LeadResponseDto> createLead(BackendLeadRequest request);
    
    Mono<List<ClinicDto>> getClinics();
    
    Mono<List<MedicalSpecialtyDto>> getMedicalSpecialties();
    
    // DTOs internos para clínicas y especialidades
    record ClinicDto(
        Long id,
        String name,
        String address,
        boolean active
    ) {}
    
    record MedicalSpecialtyDto(
        Long id,
        String name,
        String category,
        boolean active
    ) {}
} 
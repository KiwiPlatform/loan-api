package com.kiwipay.kiwipay_loan_api.leads.service;

import com.kiwipay.kiwipay_loan_api.leads.dto.request.LeadRequestDto;
import com.kiwipay.kiwipay_loan_api.leads.dto.response.ApiResponse;
import com.kiwipay.kiwipay_loan_api.leads.dto.response.LeadResponseDto;
import reactor.core.publisher.Mono;

public interface LeadService {
    
    Mono<ApiResponse<LeadResponseDto>> processLead(LeadRequestDto request);
} 
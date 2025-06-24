package com.kiwipay.kiwipay_loan_api.leads.service;

import com.kiwipay.kiwipay_loan_api.leads.dto.request.SquarespaceLeadRequest;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface SquarespaceService {
    
    Mono<Map<String, Object>> processSquarespaceLead(SquarespaceLeadRequest request);
} 
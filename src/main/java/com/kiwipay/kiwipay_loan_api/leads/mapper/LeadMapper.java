package com.kiwipay.kiwipay_loan_api.leads.mapper;

import com.kiwipay.kiwipay_loan_api.leads.dto.request.BackendLeadRequest;
import com.kiwipay.kiwipay_loan_api.leads.dto.request.LeadRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LeadMapper {
    
    BackendLeadRequest toBackendRequest(LeadRequestDto request);
} 
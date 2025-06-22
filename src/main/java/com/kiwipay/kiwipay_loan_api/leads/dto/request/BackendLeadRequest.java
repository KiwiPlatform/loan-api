package com.kiwipay.kiwipay_loan_api.leads.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BackendLeadRequest {
    
    @JsonProperty("receptionistName")
    private String receptionistName;
    
    @JsonProperty("clientName")
    private String clientName;
    
    @JsonProperty("clinicId")
    private Long clinicId;
    
    @JsonProperty("medicalSpecialtyId")
    private Long medicalSpecialtyId;
    
    @JsonProperty("dni")
    private String dni;
    
    @JsonProperty("monthlyIncome")
    private BigDecimal monthlyIncome;
    
    @JsonProperty("treatmentCost")
    private BigDecimal treatmentCost;
    
    @JsonProperty("phone")
    private String phone;
    
    @JsonProperty("email")
    private String email;
} 
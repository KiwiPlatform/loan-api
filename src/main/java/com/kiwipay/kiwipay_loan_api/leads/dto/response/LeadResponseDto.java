package com.kiwipay.kiwipay_loan_api.leads.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeadResponseDto {
    
    private Long id;
    private String receptionistName;
    private String clientName;
    private String dni;
    private String phone;
    private String email;
    private BigDecimal monthlyIncome;
    private BigDecimal treatmentCost;
    private Long clinicId;
    private String clinicName;
    private Long medicalSpecialtyId;
    private String medicalSpecialtyName;
    private String status;
    private String origin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 
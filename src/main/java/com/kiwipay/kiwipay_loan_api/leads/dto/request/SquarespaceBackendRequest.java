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
public class SquarespaceBackendRequest {
    
    // Estructura EXACTA para el nuevo endpoint del backend principal
    @JsonProperty("receptionistName")
    private String receptionistName;        // Nombres y Apellidos Recepcionista
    
    @JsonProperty("sede")
    private String sede;                    // Sede (como viene del formulario)
    
    @JsonProperty("clientName")
    private String clientName;              // Nombres y Apellidos Cliente
    
    @JsonProperty("dni")
    private String dni;                     // N° DNI
    
    @JsonProperty("monthlyIncome")
    private BigDecimal monthlyIncome;       // Ingreso Mensual (como número: 3000.00)
    
    @JsonProperty("treatmentCost")
    private BigDecimal treatmentCost;       // Costo Tratamiento (como número: 5000.00)
    
    @JsonProperty("phone")
    private String phone;                   // Teléfono
    
    @JsonProperty("source")
    private String source;                  // Para identificar que viene de Squarespace
} 
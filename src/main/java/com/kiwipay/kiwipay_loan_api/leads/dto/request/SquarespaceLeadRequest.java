package com.kiwipay.kiwipay_loan_api.leads.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SquarespaceLeadRequest {
    
    // Campos EXACTOS del formulario de Squarespace - SOLO ESTOS
    private String receptionistName;        // Nombres y Apellidos Recepcionista
    private String sede;                    // Sede (Selecciona una opción)
    private String clientName;              // Nombres y Apellidos Cliente
    private String dni;                     // N° DNI (obligatorio)
    private String monthlyIncome;           // Ingreso Mensual Promedio S/. (obligatorio)
    private String treatmentCost;           // Costo Aprox. Tratamiento S/. (obligatorio)
    private String phone;                   // Teléfono (obligatorio)
    
    // NO incluimos email porque no aparece en la imagen del formulario
} 
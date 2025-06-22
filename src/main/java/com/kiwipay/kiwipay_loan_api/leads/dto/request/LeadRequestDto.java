package com.kiwipay.kiwipay_loan_api.leads.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeadRequestDto {
    
    @NotBlank(message = "Nombres y apellidos del recepcionista son requeridos")
    @Size(max = 255, message = "Nombres del recepcionista no debe exceder 255 caracteres")
    private String receptionistName;
    
    @NotBlank(message = "Nombres y apellidos del cliente son requeridos")
    @Size(max = 255, message = "Nombres del cliente no debe exceder 255 caracteres")
    private String clientName;
    
    @NotNull(message = "Clínica es requerida")
    @Positive(message = "ID de clínica debe ser un número positivo")
    private Long clinicId;
    
    @NotNull(message = "Especialidad médica es requerida")
    @Positive(message = "ID de especialidad médica debe ser un número positivo")
    private Long medicalSpecialtyId;
    
    @NotBlank(message = "DNI es requerido")
    @Pattern(regexp = "^[0-9]{8}$", message = "El DNI debe tener exactamente 8 dígitos")
    private String dni;
    
    @NotNull(message = "Ingreso mensual es requerido")
    @DecimalMin(value = "0.01", message = "Ingreso mensual debe ser mayor a 0")
    @Digits(integer = 10, fraction = 2, message = "Ingreso mensual debe tener máximo 10 dígitos enteros y 2 decimales")
    private BigDecimal monthlyIncome;
    
    @NotNull(message = "Costo del tratamiento es requerido")
    @DecimalMin(value = "0.01", message = "Costo del tratamiento debe ser mayor a 0")
    @Digits(integer = 10, fraction = 2, message = "Costo del tratamiento debe tener máximo 10 dígitos enteros y 2 decimales")
    private BigDecimal treatmentCost;
    
    @NotBlank(message = "Teléfono es requerido")
    @Pattern(regexp = "^[0-9]{9}$", message = "El teléfono debe tener exactamente 9 dígitos")
    private String phone;
    
    @Email(message = "Email debe tener formato válido")
    @Size(max = 255, message = "Email no debe exceder 255 caracteres")
    private String email;
} 
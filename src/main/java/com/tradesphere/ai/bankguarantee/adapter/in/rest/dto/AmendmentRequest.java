package com.tradesphere.ai.bankguarantee.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Request DTO for amending a Bank Guarantee
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmendmentRequest {
    @NotBlank(message = "Amendment type is required")
    private String amendmentType;
    
    private BigDecimal newAmount;
    private LocalDateTime newExpiryDate;
    private String changesDescription;
    private String remarks;
}


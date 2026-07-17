package com.tradesphere.ai.bankguarantee.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for reducing Bank Guarantee amount
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReductionRequest {
    @NotNull(message = "Reduction amount is required")
    @Positive(message = "Reduction amount must be positive")
    private BigDecimal reductionAmount;
    
    @NotNull(message = "New total amount is required")
    @Positive(message = "New total amount must be positive")
    private BigDecimal newTotalAmount;
    
    @NotBlank(message = "Reduction reason is required")
    private String reductionReason;
    
    private String supportingEvidence;
    private String beneficiaryConsent;
    private String remarks;
}


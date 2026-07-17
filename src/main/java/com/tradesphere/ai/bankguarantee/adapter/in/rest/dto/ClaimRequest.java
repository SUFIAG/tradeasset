package com.tradesphere.ai.bankguarantee.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Request DTO for registering a claim on Bank Guarantee
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimRequest {
    @NotNull(message = "Claim amount is required")
    @Positive(message = "Claim amount must be positive")
    private BigDecimal claimAmount;
    
    @NotBlank(message = "Claim currency is required")
    private String claimCurrency;

    @NotBlank(message = "Claim reason is required")
    private String claimReason;
    
    @NotBlank(message = "Claim type is required")
    private String claimType;

    private List<String> supportingDocuments;
}


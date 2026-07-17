package com.tradesphere.ai.bankguarantee.adapter.in.rest.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Request DTO for approving a claim on Bank Guarantee
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApproveClaimRequest {
    @NotNull(message = "Approved amount is required")
    @Positive(message = "Approved amount must be positive")
    private BigDecimal approvedAmount;
    
    @NotNull(message = "Payment date is required")
    private LocalDateTime paymentDate;
    
    private String approvalComments;
    private String paymentReference;
}


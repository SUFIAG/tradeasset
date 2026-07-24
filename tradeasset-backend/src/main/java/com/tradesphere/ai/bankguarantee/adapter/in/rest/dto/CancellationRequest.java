package com.tradesphere.ai.bankguarantee.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for cancelling a Bank Guarantee
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancellationRequest {
    @NotBlank(message = "Cancellation reason is required")
    private String reason;

    private String beneficiaryConsent;
    private String remarks;
    private boolean refundApplicable;
}


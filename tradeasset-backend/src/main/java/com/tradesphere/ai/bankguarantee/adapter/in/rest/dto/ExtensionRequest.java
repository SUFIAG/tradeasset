package com.tradesphere.ai.bankguarantee.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Request DTO for extending Bank Guarantee expiry date
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtensionRequest {
    @NotNull(message = "New expiry date is required")
    private LocalDateTime newExpiryDate;

    @NotBlank(message = "Extension reason is required")
    private String extensionReason;
    
    private String justification;
    private String beneficiaryConsent;
    private String remarks;
}


package com.tradesphere.ai.bankguarantee.adapter.in.rest.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Request DTO for issuing a Bank Guarantee
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssueBGRequest {
    @NotNull(message = "Issue date is required")
    private LocalDateTime issueDate;
    
    private LocalDateTime effectiveDate;
    
    @NotNull(message = "Issuing bank is required")
    private String issuingBank;
    
    private String advisingBank;
    
    private String specialInstructions;
}


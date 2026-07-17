package com.tradesphere.ai.customer.adapter.in.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * REST DTO for completing KYC
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompleteKYCRequest {
    
    @NotNull(message = "KYC expiry date is required")
    private LocalDate expiryDate;
}


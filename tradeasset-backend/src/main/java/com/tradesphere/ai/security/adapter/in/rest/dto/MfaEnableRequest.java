package com.tradesphere.ai.security.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request to enable MFA
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MfaEnableRequest {
    
    @NotBlank(message = "Verification code is required")
    private String verificationCode;
}


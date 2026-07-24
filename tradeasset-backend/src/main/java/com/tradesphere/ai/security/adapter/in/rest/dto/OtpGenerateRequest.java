package com.tradesphere.ai.security.adapter.in.rest.dto;

import com.tradesphere.ai.security.domain.model.MfaMethod;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request to generate OTP
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpGenerateRequest {
    
    @NotNull(message = "MFA method is required")
    private MfaMethod method;
}


package com.tradesphere.ai.security.adapter.in.rest.dto;

import com.tradesphere.ai.security.domain.model.MfaMethod;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request to setup MFA
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MfaSetupRequest {
    
    @NotNull(message = "MFA method is required")
    private MfaMethod method;
    
    private String phoneNumber;  // Required for SMS
    private String email;        // Required for Email
}


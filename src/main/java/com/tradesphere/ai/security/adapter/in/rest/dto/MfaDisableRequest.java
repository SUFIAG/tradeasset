package com.tradesphere.ai.security.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request to disable MFA
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MfaDisableRequest {
    
    @NotBlank(message = "Password is required")
    private String password;
}


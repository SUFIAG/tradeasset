package com.tradesphere.ai.security.adapter.in.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response for MFA verification
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MfaVerifyResponse {
    
    private boolean valid;
    private String message;
}


package com.tradesphere.ai.security.application.port.in.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Command to verify MFA code
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyMfaCommand {
    
    private String userId;
    private String tenantId;
    private String code;
}


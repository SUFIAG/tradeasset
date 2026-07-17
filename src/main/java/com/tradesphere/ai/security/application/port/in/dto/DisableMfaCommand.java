package com.tradesphere.ai.security.application.port.in.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Command to disable MFA
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisableMfaCommand {

    private String userId;
    private String tenantId;
    private String password;  // For verification
}


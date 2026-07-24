package com.tradesphere.ai.security.application.port.in.dto;

import com.tradesphere.ai.security.domain.model.MfaMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Command to setup MFA
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MfaSetupCommand {
    
    private String userId;
    private String tenantId;
    private MfaMethod method;
    private String phoneNumber;  // for SMS
    private String email;        // for Email
}


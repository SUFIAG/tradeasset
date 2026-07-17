package com.tradesphere.ai.security.application.port.in.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response from MFA setup
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MfaSetupResponse {
    
    private String secret;
    private String qrCodeUrl;  // For TOTP
    private List<String> backupCodes;
    private String method;
}


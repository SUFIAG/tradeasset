package com.tradesphere.ai.security.application.port.in.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Command to initiate SSO authentication
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SsoInitiationCommand {

    private String providerId;
    private String returnUrl;
    private String tenantId;
    private String ipAddress;
    private String userAgent;
}


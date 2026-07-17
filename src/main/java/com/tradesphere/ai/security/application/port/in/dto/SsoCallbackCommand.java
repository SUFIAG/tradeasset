package com.tradesphere.ai.security.application.port.in.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Command to process SSO callback
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SsoCallbackCommand {
    
    private String requestId;
    private String providerId;
    private String tenantId;
    private Map<String, String> parameters;
    private String ipAddress;
    private String userAgent;
}


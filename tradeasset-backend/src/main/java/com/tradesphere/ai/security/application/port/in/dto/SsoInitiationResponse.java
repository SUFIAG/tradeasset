package com.tradesphere.ai.security.application.port.in.dto;

import com.tradesphere.ai.security.domain.model.SsoProtocol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response from SSO initiation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SsoInitiationResponse {
    
    private String requestId;
    private String authUrl;
    private SsoProtocol protocol;
    private String providerId;
}


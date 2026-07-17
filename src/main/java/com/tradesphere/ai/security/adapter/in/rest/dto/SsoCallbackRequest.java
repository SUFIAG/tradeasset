package com.tradesphere.ai.security.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Request for SSO callback
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SsoCallbackRequest {
    
    @NotBlank(message = "Request ID is required")
    private String requestId;
    
    @NotBlank(message = "Provider ID is required")
    private String providerId;
    
    @NotNull(message = "Parameters are required")
    private Map<String, String> parameters;
}


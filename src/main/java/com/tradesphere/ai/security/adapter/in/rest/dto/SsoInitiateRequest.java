package com.tradesphere.ai.security.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request to initiate SSO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SsoInitiateRequest {

    @NotBlank(message = "Provider ID is required")
    private String providerId;

    @NotBlank(message = "Return URL is required")
    private String returnUrl;
}


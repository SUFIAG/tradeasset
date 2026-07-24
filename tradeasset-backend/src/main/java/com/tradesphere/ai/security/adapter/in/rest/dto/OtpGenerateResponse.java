package com.tradesphere.ai.security.adapter.in.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response for OTP generation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpGenerateResponse {
    
    private String message;
    private int expiresInSeconds;
}


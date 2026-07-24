package com.tradesphere.ai.security.adapter.in.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response for session validation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionValidationResponse {
    
    private boolean valid;
    private String sessionId;
}


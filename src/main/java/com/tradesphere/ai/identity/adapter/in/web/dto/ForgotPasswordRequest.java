package com.tradesphere.ai.identity.adapter.in.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request DTO for forgot password endpoint
 */
@Data
public class ForgotPasswordRequest {
    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is required")
    private String email;
}


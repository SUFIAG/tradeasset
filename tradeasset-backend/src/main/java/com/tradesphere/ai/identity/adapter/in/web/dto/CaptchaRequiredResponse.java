package com.tradesphere.ai.identity.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Response DTO for CAPTCHA requirement check
 */
@Data
@AllArgsConstructor
public class CaptchaRequiredResponse {
    private boolean required;
}


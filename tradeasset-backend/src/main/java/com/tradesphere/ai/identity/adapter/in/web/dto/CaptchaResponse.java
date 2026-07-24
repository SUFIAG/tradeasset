package com.tradesphere.ai.identity.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Response DTO for CAPTCHA generation
 */
@Data
@AllArgsConstructor
public class CaptchaResponse {
    private String challengeId;
    private String question;
}


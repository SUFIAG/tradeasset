package com.tradesphere.ai.security.application.port.in.dto;

import com.tradesphere.ai.security.domain.model.MfaMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Command to generate OTP
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateOtpCommand {

    private String userId;
    private String tenantId;
    private MfaMethod method;
}


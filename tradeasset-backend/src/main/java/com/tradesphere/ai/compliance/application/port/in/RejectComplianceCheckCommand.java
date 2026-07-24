package com.tradesphere.ai.compliance.application.port.in;

import lombok.Value;

import java.util.UUID;

/**
 * Command to reject a compliance check
 */
@Value
public class RejectComplianceCheckCommand {
    UUID tenantId;
    UUID complianceCheckId;
    UUID rejectedBy;
    String reason;
}


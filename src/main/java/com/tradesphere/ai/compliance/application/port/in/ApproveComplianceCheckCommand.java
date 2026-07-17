package com.tradesphere.ai.compliance.application.port.in;

import lombok.Value;

import java.util.UUID;

/**
 * Command to approve a compliance check
 */
@Value
public class ApproveComplianceCheckCommand {
    UUID tenantId;
    UUID complianceCheckId;
    UUID approvedBy;
    String reason;
}


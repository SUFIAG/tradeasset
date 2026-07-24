package com.tradesphere.ai.compliance.application.port.in;

import lombok.Value;

import java.util.UUID;

/**
 * Command to clear a hit as false positive
 */
@Value
public class ClearFalsePositiveCommand {
    UUID tenantId;
    UUID complianceCheckId;
    UUID clearedBy;
    String reason;
}


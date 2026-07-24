package com.tradesphere.ai.compliance.application.port.in;

import com.tradesphere.ai.compliance.domain.model.ComplianceCheckType;
import lombok.Value;

import java.util.UUID;

/**
 * Command to initiate a compliance check
 */
@Value
public class InitiateComplianceCheckCommand {
    UUID tenantId;
    ComplianceCheckType checkType;
    String entityType;
    UUID entityId;
    String entityName;
    UUID initiatedBy;

    // Entity-specific data for screening
    String country;
    String dateOfBirth;
    String position;
    String transactionDetails;
}


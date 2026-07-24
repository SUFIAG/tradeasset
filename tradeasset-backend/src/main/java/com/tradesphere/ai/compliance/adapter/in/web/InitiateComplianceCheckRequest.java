package com.tradesphere.ai.compliance.adapter.in.web;

import com.tradesphere.ai.compliance.domain.model.ComplianceCheckType;
import lombok.Data;

import java.util.UUID;

/**
 * Request DTO for initiating compliance checks
 */
@Data
public class InitiateComplianceCheckRequest {
    private ComplianceCheckType checkType;
    private String entityType;
    private UUID entityId;
    private String entityName;
    private UUID initiatedBy;
    private String country;
    private String dateOfBirth;
    private String position;
    private String transactionDetails;
}


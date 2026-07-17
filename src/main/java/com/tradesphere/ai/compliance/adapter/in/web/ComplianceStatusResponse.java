package com.tradesphere.ai.compliance.adapter.in.web;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Response DTO for compliance status
 */
@Data
@AllArgsConstructor
public class ComplianceStatusResponse {
    private boolean passed;
}


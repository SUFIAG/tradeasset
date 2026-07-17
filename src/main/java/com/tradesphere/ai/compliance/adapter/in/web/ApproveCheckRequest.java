package com.tradesphere.ai.compliance.adapter.in.web;

import lombok.Data;

import java.util.UUID;

/**
 * Request DTO for approving compliance checks
 */
@Data
public class ApproveCheckRequest {
    private UUID approvedBy;
    private String reason;
}


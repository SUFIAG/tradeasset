package com.tradesphere.ai.compliance.adapter.in.web;

import lombok.Data;

import java.util.UUID;

/**
 * Request DTO for rejecting compliance checks
 */
@Data
public class RejectCheckRequest {
    private UUID rejectedBy;
    private String reason;
}


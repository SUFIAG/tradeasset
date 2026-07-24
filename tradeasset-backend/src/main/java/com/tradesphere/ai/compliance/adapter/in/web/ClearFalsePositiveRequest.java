package com.tradesphere.ai.compliance.adapter.in.web;

import lombok.Data;

import java.util.UUID;

/**
 * Request DTO for clearing false positives
 */
@Data
public class ClearFalsePositiveRequest {
    private UUID clearedBy;
    private String reason;
}


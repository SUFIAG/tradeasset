package com.tradesphere.ai.security.adapter.in.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response with backup codes
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BackupCodesResponse {

    private List<String> backupCodes;
}


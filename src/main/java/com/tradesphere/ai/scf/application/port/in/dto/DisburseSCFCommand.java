package com.tradesphere.ai.scf.application.port.in.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Command to disburse funds
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisburseSCFCommand {

    private UUID scfId;
    private String tenantId;
    private UUID disbursedBy;
}


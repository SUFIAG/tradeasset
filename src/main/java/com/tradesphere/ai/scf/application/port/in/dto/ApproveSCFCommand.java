package com.tradesphere.ai.scf.application.port.in.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Command to approve SCF financing
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApproveSCFCommand {

    private UUID scfId;
    private String tenantId;
    private UUID financierId;
    private String financierName;
    private BigDecimal discountRate;
    private UUID approvedBy;
}


package com.tradesphere.ai.scf.application.port.in.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Command to repay SCF financing
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepaySCFCommand {

    private UUID scfId;
    private String tenantId;
    private BigDecimal amount;
    private LocalDate repaymentDate;
    private UUID repaidBy;
}


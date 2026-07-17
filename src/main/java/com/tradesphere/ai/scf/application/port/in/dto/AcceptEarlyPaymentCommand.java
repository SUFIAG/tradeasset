package com.tradesphere.ai.scf.application.port.in.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Command to accept early payment offer
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AcceptEarlyPaymentCommand {

    private UUID scfId;
    private String tenantId;
    private LocalDate earlyPaymentDate;
    private BigDecimal discountPercent;
    private UUID acceptedBy;
}


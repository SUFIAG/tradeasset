package com.tradesphere.ai.bankguarantee.application.port.in.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssueBankGuaranteeCommand {
    private UUID bgId;
    private String tenantId;
    private String issuerId;
    private String issuingBank;
    private String advisingBank;
    private LocalDateTime issueDate;
    private LocalDateTime effectiveDate;
}


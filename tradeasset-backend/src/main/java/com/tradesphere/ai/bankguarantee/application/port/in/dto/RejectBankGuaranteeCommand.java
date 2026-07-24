package com.tradesphere.ai.bankguarantee.application.port.in.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RejectBankGuaranteeCommand {
    private UUID bgId;
    private String tenantId;
    private String approverId;
    private String reason;
}


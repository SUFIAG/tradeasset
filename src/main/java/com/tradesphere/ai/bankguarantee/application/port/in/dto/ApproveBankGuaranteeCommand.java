package com.tradesphere.ai.bankguarantee.application.port.in.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApproveBankGuaranteeCommand {
    private UUID bgId;
    private String tenantId;
    private String approverId;
    private String comments;
}


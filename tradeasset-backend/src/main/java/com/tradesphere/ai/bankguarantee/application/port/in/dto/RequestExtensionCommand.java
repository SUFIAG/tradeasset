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
public class RequestExtensionCommand {
    private UUID bgId;
    private String tenantId;
    private String userId;
    private LocalDateTime newExpiryDate;
    private String extensionReason;
    private String justification;
}


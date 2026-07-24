package com.tradesphere.ai.bankguarantee.application.port.in;

import com.tradesphere.ai.bankguarantee.application.port.in.dto.ClaimResponse;
import com.tradesphere.ai.bankguarantee.application.port.in.dto.RegisterClaimCommand;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Use Case: Process Claims
 */
public interface ProcessClaimUseCase {
    UUID registerClaim(RegisterClaimCommand command);
    void approveClaim(UUID claimId, String tenantId, String userId, BigDecimal approvedAmount);
    List<ClaimResponse> getClaims(UUID bgId, String tenantId);
}


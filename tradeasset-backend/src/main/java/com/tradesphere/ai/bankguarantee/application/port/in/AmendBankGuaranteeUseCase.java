package com.tradesphere.ai.bankguarantee.application.port.in;

import com.tradesphere.ai.bankguarantee.application.port.in.dto.AmendmentResponse;
import com.tradesphere.ai.bankguarantee.application.port.in.dto.RequestAmendmentCommand;

import java.util.List;
import java.util.UUID;

/**
 * Use Case: Amend Bank Guarantee
 */
public interface AmendBankGuaranteeUseCase {
    UUID requestAmendment(RequestAmendmentCommand command);
    void approveAmendment(UUID amendmentId, String tenantId, String userId);
    List<AmendmentResponse> getAmendments(UUID bgId, String tenantId);
}


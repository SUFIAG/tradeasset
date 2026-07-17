package com.tradesphere.ai.bankguarantee.application.port.in;

import com.tradesphere.ai.bankguarantee.application.port.in.dto.RequestReductionCommand;

import java.util.UUID;

/**
 * Use Case: Reduce Bank Guarantee Amount
 */
public interface ReduceBankGuaranteeUseCase {
    UUID requestReduction(RequestReductionCommand command);
}


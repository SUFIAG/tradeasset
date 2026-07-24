package com.tradesphere.ai.bankguarantee.application.port.in;

import com.tradesphere.ai.bankguarantee.application.port.in.dto.ReleaseBankGuaranteeCommand;

/**
 * Use Case: Release Bank Guarantee
 */
public interface ReleaseBankGuaranteeUseCase {
    void execute(ReleaseBankGuaranteeCommand command);
}


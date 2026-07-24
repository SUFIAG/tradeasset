package com.tradesphere.ai.bankguarantee.application.port.in;

import com.tradesphere.ai.bankguarantee.application.port.in.dto.SubmitBankGuaranteeCommand;

/**
 * Use Case: Submit Bank Guarantee for Approval
 */
public interface SubmitBankGuaranteeUseCase {
    void execute(SubmitBankGuaranteeCommand command);
}


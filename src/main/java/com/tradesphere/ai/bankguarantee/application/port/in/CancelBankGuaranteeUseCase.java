package com.tradesphere.ai.bankguarantee.application.port.in;

import com.tradesphere.ai.bankguarantee.application.port.in.dto.CancelBankGuaranteeCommand;

/**
 * Use Case: Cancel Bank Guarantee
 */
public interface CancelBankGuaranteeUseCase {
    void execute(CancelBankGuaranteeCommand command);
}


package com.tradesphere.ai.bankguarantee.application.port.in;

import com.tradesphere.ai.bankguarantee.application.port.in.dto.IssueBankGuaranteeCommand;

/**
 * Use Case: Issue Bank Guarantee
 */
public interface IssueBankGuaranteeUseCase {
    void execute(IssueBankGuaranteeCommand command);
}


package com.tradesphere.ai.bankguarantee.application.port.in;

import com.tradesphere.ai.bankguarantee.application.port.in.dto.ApproveBankGuaranteeCommand;
import com.tradesphere.ai.bankguarantee.application.port.in.dto.RejectBankGuaranteeCommand;

/**
 * Use Case: Approve/Reject Bank Guarantee
 */
public interface ApproveBankGuaranteeUseCase {
    void execute(ApproveBankGuaranteeCommand command);
    void reject(RejectBankGuaranteeCommand command);
}


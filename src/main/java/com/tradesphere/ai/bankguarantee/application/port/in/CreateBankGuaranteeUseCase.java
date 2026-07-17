package com.tradesphere.ai.bankguarantee.application.port.in;

import com.tradesphere.ai.bankguarantee.application.port.in.dto.BankGuaranteeResponse;
import com.tradesphere.ai.bankguarantee.application.port.in.dto.CreateBankGuaranteeCommand;

/**
 * Use Case: Create Bank Guarantee
 */
public interface CreateBankGuaranteeUseCase {
    BankGuaranteeResponse execute(CreateBankGuaranteeCommand command);
}


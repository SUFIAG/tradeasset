package com.tradesphere.ai.bankguarantee.application.port.in;

import com.tradesphere.ai.bankguarantee.application.port.in.dto.RequestExtensionCommand;

import java.util.UUID;

/**
 * Use Case: Extend Bank Guarantee
 */
public interface ExtendBankGuaranteeUseCase {
    UUID requestExtension(RequestExtensionCommand command);
}


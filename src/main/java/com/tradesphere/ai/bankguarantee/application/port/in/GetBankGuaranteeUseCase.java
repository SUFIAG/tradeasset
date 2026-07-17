package com.tradesphere.ai.bankguarantee.application.port.in;

import com.tradesphere.ai.bankguarantee.application.port.in.dto.BankGuaranteeResponse;
import com.tradesphere.ai.bankguarantee.domain.model.BGStatus;
import com.tradesphere.ai.bankguarantee.domain.model.BGType;

import java.util.List;
import java.util.UUID;

/**
 * Use Case: Get Bank Guarantee
 */
public interface GetBankGuaranteeUseCase {
    BankGuaranteeResponse execute(UUID id, String tenantId);
    List<BankGuaranteeResponse> listAll(String tenantId, BGType type, String status, int page, int size);
    List<BankGuaranteeResponse> getExpiringSoon(String tenantId, int days);
}


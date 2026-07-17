package com.tradesphere.ai.bankguarantee.application.port.out;

import com.tradesphere.ai.bankguarantee.domain.model.BankGuarantee;
import com.tradesphere.ai.bankguarantee.domain.model.BGStatus;
import com.tradesphere.ai.bankguarantee.domain.model.BGType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Port for Bank Guarantees
 */
public interface BankGuaranteeRepository {

    BankGuarantee save(BankGuarantee bankGuarantee);

    Optional<BankGuarantee> findById(UUID id, String tenantId);

    Optional<BankGuarantee> findByBgNumber(String bgNumber, String tenantId);

    Optional<BankGuarantee> findByReferenceNumber(String referenceNumber, String tenantId);

    List<BankGuarantee> findByTenantId(String tenantId);

    List<BankGuarantee> findByStatus(String tenantId, BGStatus status);

    List<BankGuarantee> findByType(String tenantId, BGType bgType);

    List<BankGuarantee> findByApplicant(UUID applicantId, String tenantId);

    List<BankGuarantee> findByBeneficiary(UUID beneficiaryId, String tenantId);

    List<BankGuarantee> findExpiringSoon(String tenantId, int days);

    List<BankGuarantee> findExpired(String tenantId);

    void delete(UUID id);

    boolean exists(UUID id, String tenantId);
}


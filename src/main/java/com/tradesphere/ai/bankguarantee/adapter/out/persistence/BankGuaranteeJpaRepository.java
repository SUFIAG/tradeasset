package com.tradesphere.ai.bankguarantee.adapter.out.persistence;

import com.tradesphere.ai.bankguarantee.domain.model.BGStatus;
import com.tradesphere.ai.bankguarantee.domain.model.BGType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository for Bank Guarantees
 */
@Repository
public interface BankGuaranteeJpaRepository extends JpaRepository<BankGuaranteeJpaEntity, UUID> {

    Optional<BankGuaranteeJpaEntity> findByIdAndTenantId(UUID id, String tenantId);

    Optional<BankGuaranteeJpaEntity> findByBgNumberAndTenantId(String bgNumber, String tenantId);

    Optional<BankGuaranteeJpaEntity> findByReferenceNumberAndTenantId(String referenceNumber, String tenantId);

    List<BankGuaranteeJpaEntity> findByTenantIdOrderByCreatedAtDesc(String tenantId);

    List<BankGuaranteeJpaEntity> findByTenantIdAndStatus(String tenantId, BGStatus status);

    List<BankGuaranteeJpaEntity> findByTenantIdAndBgType(String tenantId, BGType bgType);

    List<BankGuaranteeJpaEntity> findByTenantIdAndBgTypeAndStatus(String tenantId, BGType bgType, BGStatus status);

    List<BankGuaranteeJpaEntity> findByApplicantIdAndTenantId(UUID applicantId, String tenantId);

    List<BankGuaranteeJpaEntity> findByBeneficiaryIdAndTenantId(UUID beneficiaryId, String tenantId);

    @Query("SELECT bg FROM BankGuaranteeJpaEntity bg WHERE bg.tenantId = :tenantId " +
           "AND bg.status IN ('ACTIVE', 'ISSUED') " +
           "AND bg.expiryDate BETWEEN :startDate AND :endDate")
    List<BankGuaranteeJpaEntity> findExpiringGuarantees(
        @Param("tenantId") String tenantId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT bg FROM BankGuaranteeJpaEntity bg WHERE bg.tenantId = :tenantId " +
           "AND bg.status IN ('ACTIVE', 'ISSUED') AND bg.expiryDate < :now")
    List<BankGuaranteeJpaEntity> findExpiredGuarantees(
        @Param("tenantId") String tenantId,
        @Param("now") LocalDateTime now
    );

    @Query("SELECT COUNT(bg) FROM BankGuaranteeJpaEntity bg WHERE bg.tenantId = :tenantId AND bg.status = :status")
    long countByTenantIdAndStatus(@Param("tenantId") String tenantId, @Param("status") BGStatus status);

    @Query("SELECT SUM(bg.amount) FROM BankGuaranteeJpaEntity bg WHERE bg.tenantId = :tenantId " +
           "AND bg.status IN ('ACTIVE', 'ISSUED') AND bg.currency = :currency")
    java.math.BigDecimal sumActiveAmountByCurrency(
        @Param("tenantId") String tenantId,
        @Param("currency") String currency
    );
}


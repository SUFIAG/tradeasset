package com.tradesphere.ai.scf.adapter.out.persistence;

import com.tradesphere.ai.scf.domain.model.SCFStatus;
import com.tradesphere.ai.scf.domain.model.SCFType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository for Supply Chain Finance
 */
@Repository
public interface SCFJpaRepository extends JpaRepository<SCFJpaEntity, UUID> {

    Optional<SCFJpaEntity> findByIdAndTenantId(UUID id, String tenantId);

    Optional<SCFJpaEntity> findByScfReferenceAndTenantId(String scfReference, String tenantId);

    List<SCFJpaEntity> findByTenantIdOrderByCreatedAtDesc(String tenantId);

    List<SCFJpaEntity> findByTenantIdAndStatus(String tenantId, SCFStatus status);

    List<SCFJpaEntity> findByTenantIdAndType(String tenantId, SCFType type);

    List<SCFJpaEntity> findBySupplierIdAndTenantId(UUID supplierId, String tenantId);

    List<SCFJpaEntity> findByBuyerIdAndTenantId(UUID buyerId, String tenantId);

    List<SCFJpaEntity> findByFinancierIdAndTenantId(UUID financierId, String tenantId);

    @Query("SELECT s FROM SCFJpaEntity s WHERE s.tenantId = :tenantId " +
           "AND s.status IN ('APPROVED', 'FUNDED') " +
           "AND s.dueDate BETWEEN :startDate AND :endDate")
    List<SCFJpaEntity> findDueSoon(
        @Param("tenantId") String tenantId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("SELECT s FROM SCFJpaEntity s WHERE s.tenantId = :tenantId " +
           "AND s.status IN ('APPROVED', 'FUNDED') AND s.dueDate < :now")
    List<SCFJpaEntity> findOverdue(
        @Param("tenantId") String tenantId,
        @Param("now") LocalDate now
    );

    @Query("SELECT COUNT(s) FROM SCFJpaEntity s WHERE s.tenantId = :tenantId AND s.status = :status")
    long countByTenantIdAndStatus(@Param("tenantId") String tenantId, @Param("status") SCFStatus status);

    @Query("SELECT SUM(s.financeAmount) FROM SCFJpaEntity s WHERE s.tenantId = :tenantId " +
           "AND s.status IN ('APPROVED', 'FUNDED') AND s.currency = :currency")
    BigDecimal sumActiveFundingByCurrency(
        @Param("tenantId") String tenantId,
        @Param("currency") String currency
    );

    @Query("SELECT s FROM SCFJpaEntity s WHERE s.tenantId = :tenantId " +
           "AND s.invoiceNumber = :invoiceNumber")
    Optional<SCFJpaEntity> findByInvoiceNumber(
        @Param("tenantId") String tenantId,
        @Param("invoiceNumber") String invoiceNumber
    );
}


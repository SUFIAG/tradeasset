package com.tradesphere.ai.lc.adapter.out.persistence;

import com.tradesphere.ai.lc.domain.model.LCStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for LCJpaEntity
 * High-performance with tenant-aware queries
 */
@Repository
public interface LCJpaRepository extends JpaRepository<LCJpaEntity, UUID> {
    
    // Tenant-aware queries for security and performance
    Optional<LCJpaEntity> findByIdAndTenantId(UUID id, UUID tenantId);
    
    Optional<LCJpaEntity> findByLcNumberAndTenantId(String lcNumber, UUID tenantId);
    
    Optional<LCJpaEntity> findByLcNumber(String lcNumber);
    
    Page<LCJpaEntity> findByTenantIdAndApplicantId(UUID tenantId, UUID applicantId, Pageable pageable);
    
    Page<LCJpaEntity> findByApplicantId(UUID applicantId, Pageable pageable);
    
    Page<LCJpaEntity> findByTenantIdAndStatus(UUID tenantId, LCStatus status, Pageable pageable);
    
    Page<LCJpaEntity> findByStatus(LCStatus status, Pageable pageable);
    
    @Query("SELECT MAX(SUBSTRING(l.lcNumber, 9, 6)) FROM LCJpaEntity l WHERE l.tenantId = :tenantId AND SUBSTRING(l.lcNumber, 4, 4) = :year")
    String findMaxSequenceForYearAndTenantId(@Param("tenantId") UUID tenantId, @Param("year") String year);
    
    @Query("SELECT MAX(SUBSTRING(l.lcNumber, 9, 6)) FROM LCJpaEntity l WHERE SUBSTRING(l.lcNumber, 4, 4) = :year")
    String findMaxSequenceForYear(@Param("year") String year);
}


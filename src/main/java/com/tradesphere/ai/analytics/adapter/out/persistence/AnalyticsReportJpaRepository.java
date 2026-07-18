package com.tradesphere.ai.analytics.adapter.out.persistence;

import com.tradesphere.ai.analytics.domain.model.ReportType;
import com.tradesphere.ai.analytics.domain.model.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository for Analytics Reports
 * Optimized for cache retrieval and report management
 */
@Repository
public interface AnalyticsReportJpaRepository extends JpaRepository<AnalyticsReportJpaEntity, UUID> {

    /**
     * Find report by ID and tenant (multi-tenancy isolation)
     */
    Optional<AnalyticsReportJpaEntity> findByIdAndTenantId(UUID id, UUID tenantId);

    /**
     * Find reports by type for a tenant
     */
    List<AnalyticsReportJpaEntity> findByReportTypeAndTenantIdOrderByGeneratedAtDesc(
        ReportType reportType, 
        UUID tenantId
    );

    /**
     * Find all reports for a tenant
     */
    List<AnalyticsReportJpaEntity> findByTenantIdOrderByGeneratedAtDesc(UUID tenantId);

    /**
     * Find cached reports that are still valid
     */
    @Query("SELECT r FROM AnalyticsReportJpaEntity r WHERE r.tenantId = :tenantId " +
           "AND r.cached = true " +
           "AND r.cacheExpiresAt > :now " +
           "AND r.status = 'COMPLETED' " +
           "ORDER BY r.generatedAt DESC")
    List<AnalyticsReportJpaEntity> findValidCachedReports(
        @Param("tenantId") UUID tenantId,
        @Param("now") Instant now
    );

    /**
     * Find cached report by type and date range
     */
    @Query("SELECT r FROM AnalyticsReportJpaEntity r WHERE r.tenantId = :tenantId " +
           "AND r.reportType = :reportType " +
           "AND r.startDate = :startDate " +
           "AND r.endDate = :endDate " +
           "AND r.cached = true " +
           "AND r.cacheExpiresAt > :now " +
           "AND r.status = 'COMPLETED' " +
           "ORDER BY r.generatedAt DESC")
    Optional<AnalyticsReportJpaEntity> findCachedReport(
        @Param("tenantId") UUID tenantId,
        @Param("reportType") ReportType reportType,
        @Param("startDate") java.time.LocalDate startDate,
        @Param("endDate") java.time.LocalDate endDate,
        @Param("now") Instant now
    );

    /**
     * Find reports by status
     */
    List<AnalyticsReportJpaEntity> findByStatusAndTenantId(
        ReportStatus status, 
        UUID tenantId
    );

    /**
     * Delete expired cached reports (cleanup job)
     */
    @Modifying
    @Query("DELETE FROM AnalyticsReportJpaEntity r WHERE r.cached = true " +
           "AND r.cacheExpiresAt < :now")
    void deleteExpiredCachedReports(@Param("now") Instant now);

    /**
     * Count reports by type for a tenant
     */
    long countByReportTypeAndTenantId(ReportType reportType, UUID tenantId);

    /**
     * Find recent reports (for dashboard)
     */
    @Query("SELECT r FROM AnalyticsReportJpaEntity r WHERE r.tenantId = :tenantId " +
           "AND r.status = 'COMPLETED' " +
           "ORDER BY r.generatedAt DESC")
    List<AnalyticsReportJpaEntity> findRecentReports(
        @Param("tenantId") UUID tenantId
    );
}


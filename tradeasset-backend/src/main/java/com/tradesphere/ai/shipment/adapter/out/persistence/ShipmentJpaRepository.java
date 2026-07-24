package com.tradesphere.ai.shipment.adapter.out.persistence;

import com.tradesphere.ai.shipment.domain.model.ShipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository for Shipment
 * Optimized queries for high-throughput tracking
 */
@Repository
public interface ShipmentJpaRepository extends JpaRepository<ShipmentJpaEntity, UUID> {

    Optional<ShipmentJpaEntity> findByIdAndTenantId(UUID id, UUID tenantId);

    Optional<ShipmentJpaEntity> findByShipmentNumberAndTenantId(String shipmentNumber, UUID tenantId);

    List<ShipmentJpaEntity> findByReferenceNumberAndTenantId(String referenceNumber, UUID tenantId);

    @Query("SELECT s FROM ShipmentJpaEntity s WHERE s.tenantId = :tenantId " +
           "AND s.status NOT IN ('DELIVERED', 'CANCELLED') " +
           "ORDER BY s.estimatedArrival ASC")
    List<ShipmentJpaEntity> findActiveShipments(@Param("tenantId") UUID tenantId);

    @Query("SELECT s FROM ShipmentJpaEntity s WHERE s.status = :status AND s.tenantId = :tenantId " +
           "ORDER BY s.estimatedArrival ASC")
    List<ShipmentJpaEntity> findByStatusAndTenantId(
        @Param("status") ShipmentStatus status,
        @Param("tenantId") UUID tenantId
    );

    @Query("SELECT s FROM ShipmentJpaEntity s WHERE s.tenantId = :tenantId " +
           "AND s.estimatedArrival < :now " +
           "AND s.status NOT IN ('DELIVERED', 'CANCELLED', 'ARRIVED') " +
           "ORDER BY s.estimatedArrival ASC")
    List<ShipmentJpaEntity> findDelayedShipments(
        @Param("tenantId") UUID tenantId,
        @Param("now") Instant now
    );

    @Query("SELECT COUNT(s) FROM ShipmentJpaEntity s WHERE s.tenantId = :tenantId " +
           "AND s.status NOT IN ('DELIVERED', 'CANCELLED')")
    long countActiveByTenantId(@Param("tenantId") UUID tenantId);
}


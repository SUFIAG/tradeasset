package com.tradesphere.ai.shipment.application.port.out;

import com.tradesphere.ai.shipment.domain.model.Shipment;
import com.tradesphere.ai.shipment.domain.model.ShipmentId;
import com.tradesphere.ai.shipment.domain.model.ShipmentStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Shipment Repository Port (Hexagonal Architecture)
 * Optimized for high-throughput tracking queries
 */
public interface ShipmentRepository {

    /**
     * Save shipment (optimized for write-heavy workload)
     */
    Shipment save(Shipment shipment);

    /**
     * Find by ID with tenant isolation
     */
    Optional<Shipment> findById(ShipmentId id, UUID tenantId);

    /**
     * Find by shipment number (indexed for fast lookup)
     */
    Optional<Shipment> findByShipmentNumber(String shipmentNumber, UUID tenantId);

    /**
     * Find by reference (LC/Contract reference)
     */
    List<Shipment> findByReference(String referenceNumber, UUID tenantId);

    /**
     * Find active shipments (optimized for dashboard queries)
     */
    List<Shipment> findActiveShipments(UUID tenantId, int limit);

    /**
     * Find by status (indexed for fast filtering)
     */
    List<Shipment> findByStatus(ShipmentStatus status, UUID tenantId, int limit);

    /**
     * Find delayed shipments (for alert system)
     */
    List<Shipment> findDelayedShipments(UUID tenantId);

    /**
     * Count active shipments (for metrics)
     */
    long countActive(UUID tenantId);
}


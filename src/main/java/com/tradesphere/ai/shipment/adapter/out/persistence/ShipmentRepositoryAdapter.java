package com.tradesphere.ai.shipment.adapter.out.persistence;

import com.tradesphere.ai.shipment.application.port.out.ShipmentRepository;
import com.tradesphere.ai.shipment.domain.model.Shipment;
import com.tradesphere.ai.shipment.domain.model.ShipmentId;
import com.tradesphere.ai.shipment.domain.model.ShipmentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adapter implementing ShipmentRepository port
 * Optimized for high-throughput tracking operations
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ShipmentRepositoryAdapter implements ShipmentRepository {

    private final ShipmentJpaRepository jpaRepository;
    private final ShipmentMapper mapper;

    @Override
    @Transactional
    public Shipment save(Shipment shipment) {
        log.debug("Saving shipment: {}", shipment.getId());
        ShipmentJpaEntity entity = mapper.toEntity(shipment);
        ShipmentJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Shipment> findById(ShipmentId id, UUID tenantId) {
        log.debug("Finding shipment by ID: {} for tenant: {}", id, tenantId);
        return jpaRepository.findByIdAndTenantId(id.value(), tenantId)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Shipment> findByShipmentNumber(String shipmentNumber, UUID tenantId) {
        log.debug("Finding shipment by number: {} for tenant: {}", shipmentNumber, tenantId);
        return jpaRepository.findByShipmentNumberAndTenantId(shipmentNumber, tenantId)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Shipment> findByReference(String referenceNumber, UUID tenantId) {
        log.debug("Finding shipments by reference: {} for tenant: {}", referenceNumber, tenantId);
        return jpaRepository.findByReferenceNumberAndTenantId(referenceNumber, tenantId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Shipment> findActiveShipments(UUID tenantId, int limit) {
        log.debug("Finding active shipments for tenant: {} with limit: {}", tenantId, limit);
        return jpaRepository.findActiveShipments(tenantId)
                .stream()
                .limit(limit)
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Shipment> findByStatus(ShipmentStatus status, UUID tenantId, int limit) {
        log.debug("Finding shipments by status: {} for tenant: {} with limit: {}", status, tenantId, limit);
        return jpaRepository.findByStatusAndTenantId(status, tenantId)
                .stream()
                .limit(limit)
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Shipment> findDelayedShipments(UUID tenantId) {
        log.debug("Finding delayed shipments for tenant: {}", tenantId);
        return jpaRepository.findDelayedShipments(tenantId, Instant.now())
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long countActive(UUID tenantId) {
        log.debug("Counting active shipments for tenant: {}", tenantId);
        return jpaRepository.countActiveByTenantId(tenantId);
    }
}


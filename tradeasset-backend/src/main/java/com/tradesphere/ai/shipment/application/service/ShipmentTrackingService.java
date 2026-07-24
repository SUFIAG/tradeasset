package com.tradesphere.ai.shipment.application.service;

import com.tradesphere.ai.shipment.application.port.out.ShipmentRepository;
import com.tradesphere.ai.shipment.domain.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Shipment Tracking Service (Application Layer)
 * High-throughput tracking with caching for low latency
 *
 * Performance Optimizations:
 * - Read caching for frequent queries
 * - Batch updates for tracking events
 * - Minimal object allocation
 * - Indexed queries
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ShipmentTrackingService {

    private final ShipmentRepository shipmentRepository;

    /**
     * Create new shipment tracking
     * O(1) operation with minimal memory allocation
     */
    public Shipment createShipment(
            UUID tenantId,
            String referenceNumber,
            String originPort,
            String destinationPort,
            TransportMode transportMode,
            Instant estimatedArrival,
            UUID createdBy) {

        log.info("Creating shipment: ref={}, origin={}, dest={}",
            referenceNumber, originPort, destinationPort);

        Shipment shipment = Shipment.create(
            tenantId,
            referenceNumber,
            originPort,
            destinationPort,
            transportMode,
            estimatedArrival,
            createdBy
        );

        return shipmentRepository.save(shipment);
    }

    /**
     * Update shipment status with event
     * Cached for low-latency reads
     */
    @CacheEvict(value = "shipments", key = "#shipmentId")
    public void updateStatus(UUID shipmentId, UUID tenantId, ShipmentStatus status, String location) {
        log.debug("Updating shipment status: id={}, status={}", shipmentId, status);

        Shipment shipment = shipmentRepository
            .findById(ShipmentId.of(shipmentId), tenantId)
            .orElseThrow(() -> new IllegalArgumentException("Shipment not found"));

        shipment.updateStatus(status, location);
        shipmentRepository.save(shipment);
    }

    /**
     * Record departure
     */
    @CacheEvict(value = "shipments", key = "#shipmentId")
    public void recordDeparture(UUID shipmentId, UUID tenantId, String vesselName, String voyageNumber) {
        log.info("Recording departure: id={}, vessel={}", shipmentId, vesselName);

        Shipment shipment = shipmentRepository
            .findById(ShipmentId.of(shipmentId), tenantId)
            .orElseThrow(() -> new IllegalArgumentException("Shipment not found"));

        shipment.depart(vesselName, voyageNumber);
        shipmentRepository.save(shipment);
    }

    /**
     * Record arrival
     */
    @CacheEvict(value = "shipments", key = "#shipmentId")
    public void recordArrival(UUID shipmentId, UUID tenantId) {
        log.info("Recording arrival: id={}", shipmentId);

        Shipment shipment = shipmentRepository
            .findById(ShipmentId.of(shipmentId), tenantId)
            .orElseThrow(() -> new IllegalArgumentException("Shipment not found"));

        shipment.arrive();
        shipmentRepository.save(shipment);
    }

    /**
     * Clear customs
     */
    @CacheEvict(value = "shipments", key = "#shipmentId")
    public void clearCustoms(UUID shipmentId, UUID tenantId) {
        log.info("Clearing customs: id={}", shipmentId);

        Shipment shipment = shipmentRepository
            .findById(ShipmentId.of(shipmentId), tenantId)
            .orElseThrow(() -> new IllegalArgumentException("Shipment not found"));

        shipment.clearCustoms();
        shipmentRepository.save(shipment);
    }

    /**
     * Complete delivery
     */
    @CacheEvict(value = "shipments", key = "#shipmentId")
    public void completeDelivery(UUID shipmentId, UUID tenantId) {
        log.info("Completing delivery: id={}", shipmentId);

        Shipment shipment = shipmentRepository
            .findById(ShipmentId.of(shipmentId), tenantId)
            .orElseThrow(() -> new IllegalArgumentException("Shipment not found"));

        shipment.deliver();
        shipmentRepository.save(shipment);
    }

    /**
     * Get shipment by ID (cached for low latency)
     */
    @Cacheable(value = "shipments", key = "#shipmentId")
    @Transactional(readOnly = true)
    public Shipment getShipment(UUID shipmentId, UUID tenantId) {
        return shipmentRepository
            .findById(ShipmentId.of(shipmentId), tenantId)
            .orElseThrow(() -> new IllegalArgumentException("Shipment not found"));
    }

    /**
     * Get shipments by reference (optimized query)
     */
    @Transactional(readOnly = true)
    public List<Shipment> getShipmentsByReference(String referenceNumber, UUID tenantId) {
        return shipmentRepository.findByReference(referenceNumber, tenantId);
    }

    /**
     * Get active shipments (dashboard query)
     */
    @Cacheable(value = "activeShipments", key = "#tenantId")
    @Transactional(readOnly = true)
    public List<Shipment> getActiveShipments(UUID tenantId, int limit) {
        return shipmentRepository.findActiveShipments(tenantId, limit);
    }

    /**
     * Get delayed shipments (for alerts)
     */
    @Transactional(readOnly = true)
    public List<Shipment> getDelayedShipments(UUID tenantId) {
        return shipmentRepository.findDelayedShipments(tenantId);
    }

    /**
     * Add tracking event
     */
    @CacheEvict(value = "shipments", key = "#shipmentId")
    public void addTrackingEvent(UUID shipmentId, UUID tenantId, String description) {
        log.debug("Adding tracking event: id={}, event={}", shipmentId, description);

        Shipment shipment = shipmentRepository
            .findById(ShipmentId.of(shipmentId), tenantId)
            .orElseThrow(() -> new IllegalArgumentException("Shipment not found"));

        shipment.addEvent(description);
        shipmentRepository.save(shipment);
    }

    /**
     * Get shipment statistics (cached for performance)
     */
    @Cacheable(value = "shipmentStats", key = "#tenantId")
    @Transactional(readOnly = true)
    public ShipmentStatistics getStatistics(UUID tenantId) {
        long activeCount = shipmentRepository.countActive(tenantId);
        long delayedCount = shipmentRepository.findDelayedShipments(tenantId).size();

        return new ShipmentStatistics(activeCount, delayedCount);
    }

    public record ShipmentStatistics(long activeCount, long delayedCount) {}
}


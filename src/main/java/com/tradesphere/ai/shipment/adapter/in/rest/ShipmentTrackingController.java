package com.tradesphere.ai.shipment.adapter.in.rest;

import com.tradesphere.ai.shipment.application.service.ShipmentTrackingService;
import com.tradesphere.ai.shipment.domain.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Shipment Tracking REST API (Adapter Layer)
 * High-throughput endpoint with response caching
 */
@RestController
@RequestMapping("/api/v1/shipments")
@RequiredArgsConstructor
@Slf4j
public class ShipmentTrackingController {

    private final ShipmentTrackingService trackingService;

    /**
     * Create new shipment tracking
     */
    @PostMapping
    public ResponseEntity<ShipmentResponse> createShipment(
            @RequestHeader("X-Tenant-ID") UUID tenantId,
            @RequestHeader("X-User-ID") UUID userId,
            @RequestBody CreateShipmentRequest request) {

        Shipment shipment = trackingService.createShipment(
            tenantId,
            request.referenceNumber(),
            request.originPort(),
            request.destinationPort(),
            request.transportMode(),
            request.estimatedArrival(),
            userId
        );

        return ResponseEntity.ok(ShipmentResponse.from(shipment));
    }

    /**
     * Get shipment by ID
     */
    @GetMapping("/{shipmentId}")
    public ResponseEntity<ShipmentResponse> getShipment(
            @RequestHeader("X-Tenant-ID") UUID tenantId,
            @PathVariable UUID shipmentId) {

        Shipment shipment = trackingService.getShipment(shipmentId, tenantId);
        return ResponseEntity.ok(ShipmentResponse.from(shipment));
    }

    /**
     * Get shipments by reference
     */
    @GetMapping("/reference/{referenceNumber}")
    public ResponseEntity<List<ShipmentResponse>> getByReference(
            @RequestHeader("X-Tenant-ID") UUID tenantId,
            @PathVariable String referenceNumber) {

        List<Shipment> shipments = trackingService.getShipmentsByReference(referenceNumber, tenantId);
        return ResponseEntity.ok(shipments.stream().map(ShipmentResponse::from).toList());
    }

    /**
     * Get active shipments
     */
    @GetMapping("/active")
    public ResponseEntity<List<ShipmentResponse>> getActiveShipments(
            @RequestHeader("X-Tenant-ID") UUID tenantId,
            @RequestParam(defaultValue = "50") int limit) {

        List<Shipment> shipments = trackingService.getActiveShipments(tenantId, limit);
        return ResponseEntity.ok(shipments.stream().map(ShipmentResponse::from).toList());
    }

    /**
     * Get delayed shipments
     */
    @GetMapping("/delayed")
    public ResponseEntity<List<ShipmentResponse>> getDelayedShipments(
            @RequestHeader("X-Tenant-ID") UUID tenantId) {

        List<Shipment> shipments = trackingService.getDelayedShipments(tenantId);
        return ResponseEntity.ok(shipments.stream().map(ShipmentResponse::from).toList());
    }

    /**
     * Record departure
     */
    @PostMapping("/{shipmentId}/depart")
    public ResponseEntity<Void> recordDeparture(
            @RequestHeader("X-Tenant-ID") UUID tenantId,
            @PathVariable UUID shipmentId,
            @RequestBody DepartureRequest request) {

        trackingService.recordDeparture(shipmentId, tenantId, request.vesselName(), request.voyageNumber());
        return ResponseEntity.ok().build();
    }

    /**
     * Record arrival
     */
    @PostMapping("/{shipmentId}/arrive")
    public ResponseEntity<Void> recordArrival(
            @RequestHeader("X-Tenant-ID") UUID tenantId,
            @PathVariable UUID shipmentId) {

        trackingService.recordArrival(shipmentId, tenantId);
        return ResponseEntity.ok().build();
    }

    /**
     * Clear customs
     */
    @PostMapping("/{shipmentId}/clear-customs")
    public ResponseEntity<Void> clearCustoms(
            @RequestHeader("X-Tenant-ID") UUID tenantId,
            @PathVariable UUID shipmentId) {

        trackingService.clearCustoms(shipmentId, tenantId);
        return ResponseEntity.ok().build();
    }

    /**
     * Complete delivery
     */
    @PostMapping("/{shipmentId}/deliver")
    public ResponseEntity<Void> completeDelivery(
            @RequestHeader("X-Tenant-ID") UUID tenantId,
            @PathVariable UUID shipmentId) {

        trackingService.completeDelivery(shipmentId, tenantId);
        return ResponseEntity.ok().build();
    }

    /**
     * Add tracking event
     */
    @PostMapping("/{shipmentId}/events")
    public ResponseEntity<Void> addTrackingEvent(
            @RequestHeader("X-Tenant-ID") UUID tenantId,
            @PathVariable UUID shipmentId,
            @RequestBody TrackingEventRequest request) {

        trackingService.addTrackingEvent(shipmentId, tenantId, request.description());
        return ResponseEntity.ok().build();
    }

    /**
     * Get statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<ShipmentTrackingService.ShipmentStatistics> getStatistics(
            @RequestHeader("X-Tenant-ID") UUID tenantId) {

        return ResponseEntity.ok(trackingService.getStatistics(tenantId));
    }

    // DTOs

    public record CreateShipmentRequest(
        String referenceNumber,
        String originPort,
        String destinationPort,
        TransportMode transportMode,
        Instant estimatedArrival
    ) {}

    public record DepartureRequest(String vesselName, String voyageNumber) {}

    public record TrackingEventRequest(String description) {}

    public record ShipmentResponse(
        UUID id,
        String shipmentNumber,
        String referenceNumber,
        ShipmentStatus status,
        String originPort,
        String destinationPort,
        TransportMode transportMode,
        Instant estimatedArrival,
        Instant actualArrival,
        String vesselName,
        String voyageNumber,
        boolean delayed,
        List<TrackingEventDTO> events
    ) {
        public static ShipmentResponse from(Shipment shipment) {
            return new ShipmentResponse(
                shipment.getId().value(),
                shipment.getShipmentNumber(),
                shipment.getReferenceNumber(),
                shipment.getStatus(),
                shipment.getOriginPort(),
                shipment.getDestinationPort(),
                shipment.getTransportMode(),
                shipment.getEstimatedArrival(),
                shipment.getActualArrival(),
                shipment.getVesselName(),
                shipment.getVoyageNumber(),
                shipment.isDelayed(),
                shipment.getTrackingEvents().stream()
                    .map(e -> new TrackingEventDTO(e.getTimestamp(), e.getDescription(), e.getStatus()))
                    .toList()
            );
        }
    }

    public record TrackingEventDTO(Instant timestamp, String description, String status) {}
}


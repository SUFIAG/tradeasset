package com.tradesphere.ai.shipment.domain.model;

import com.tradesphere.ai.shared.domain.AggregateRoot;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Shipment Aggregate Root
 * Tracks goods shipment from origin to destination with real-time updates
 *
 * Design: Optimized for high-throughput tracking with minimal memory footprint
 */
@Getter
@Setter(AccessLevel.PRIVATE)
public class Shipment extends AggregateRoot<ShipmentId> {

    // Core Identity
    private UUID tenantId;
    private String shipmentNumber;
    private String referenceNumber;  // LC/Contract reference

    // Status & Lifecycle
    private ShipmentStatus status;
    private Instant estimatedArrival;
    private Instant actualArrival;

    // Route Information (optimized for memory)
    private String originPort;
    private String originCountry;
    private String destinationPort;
    private String destinationCountry;

    // Transport Details
    private TransportMode transportMode;
    private String vesselName;
    private String voyageNumber;
    private String containerNumber;

    // Tracking (lazy-loaded list to save memory)
    private List<TrackingEvent> trackingEvents;

    // Parties (IDs only for low memory)
    private UUID shipperId;
    private UUID consigneeId;
    private UUID carrierId;

    // Documents
    private String billOfLadingNumber;
    private boolean documentsReceived;

    // Milestones (bit flags for memory efficiency)
    private byte milestoneFlags;  // Packed milestone status

    private Shipment(ShipmentId id) {
        super(id);
        this.trackingEvents = new ArrayList<>(10);  // Pre-sized for typical events
        this.milestoneFlags = 0;
    }

    /**
     * Create new shipment with minimal memory allocation
     */
    public static Shipment create(
            UUID tenantId,
            String referenceNumber,
            String originPort,
            String destinationPort,
            TransportMode transportMode,
            Instant estimatedArrival,
            UUID createdBy) {

        Shipment shipment = new Shipment(ShipmentId.generate());
        shipment.tenantId = tenantId;
        shipment.shipmentNumber = generateShipmentNumber();
        shipment.referenceNumber = referenceNumber;
        shipment.originPort = originPort;
        shipment.destinationPort = destinationPort;
        shipment.transportMode = transportMode;
        shipment.estimatedArrival = estimatedArrival;
        shipment.status = ShipmentStatus.CREATED;
        shipment.setCreatedBy(createdBy);
        shipment.setCreatedAt(Instant.now());

        shipment.addEvent("Shipment created");
        return shipment;
    }

    /**
     * Add tracking event (optimized for high-throughput updates)
     */
    public void addEvent(String description) {
        TrackingEvent event = new TrackingEvent(
            Instant.now(),
            description,
            status.name(),
            null  // Location can be null to save memory
        );

        // Limit event list size to prevent memory bloat
        if (trackingEvents.size() >= 50) {
            trackingEvents.remove(0);  // Remove oldest
        }
        trackingEvents.add(event);
    }

    /**
     * Update shipment status with event
     */
    public void updateStatus(ShipmentStatus newStatus, String location) {
        this.status = newStatus;
        addEvent("Status changed to " + newStatus + " at " + location);

        // Update milestone flags
        updateMilestoneFlag(newStatus);
    }

    /**
     * Depart from origin
     */
    public void depart(String vesselName, String voyageNumber) {
        if (status != ShipmentStatus.CREATED && status != ShipmentStatus.READY) {
            throw new IllegalStateException("Invalid status for departure");
        }

        this.status = ShipmentStatus.IN_TRANSIT;
        this.vesselName = vesselName;
        this.voyageNumber = voyageNumber;

        addEvent("Departed from " + originPort + " on vessel " + vesselName);
        updateMilestoneFlag(ShipmentStatus.IN_TRANSIT);
    }

    /**
     * Arrive at destination
     */
    public void arrive() {
        if (status != ShipmentStatus.IN_TRANSIT) {
            throw new IllegalStateException("Shipment must be in transit");
        }

        this.status = ShipmentStatus.ARRIVED;
        this.actualArrival = Instant.now();

        addEvent("Arrived at " + destinationPort);
        updateMilestoneFlag(ShipmentStatus.ARRIVED);
    }

    /**
     * Clear customs
     */
    public void clearCustoms() {
        if (status != ShipmentStatus.ARRIVED) {
            throw new IllegalStateException("Shipment must arrive first");
        }

        this.status = ShipmentStatus.CUSTOMS_CLEARED;
        addEvent("Customs cleared at " + destinationPort);
        updateMilestoneFlag(ShipmentStatus.CUSTOMS_CLEARED);
    }

    /**
     * Complete delivery
     */
    public void deliver() {
        if (status != ShipmentStatus.CUSTOMS_CLEARED) {
            throw new IllegalStateException("Customs must be cleared");
        }

        this.status = ShipmentStatus.DELIVERED;
        addEvent("Delivered to consignee");
        updateMilestoneFlag(ShipmentStatus.DELIVERED);
    }

    /**
     * Check if shipment is delayed
     */
    public boolean isDelayed() {
        return estimatedArrival != null &&
               Instant.now().isAfter(estimatedArrival) &&
               !status.isTerminal();
    }

    /**
     * Get delay duration in seconds
     */
    public long getDelaySeconds() {
        if (!isDelayed()) return 0;
        return Instant.now().getEpochSecond() - estimatedArrival.getEpochSecond();
    }

    /**
     * Update milestone flags (memory-efficient bit packing)
     */
    private void updateMilestoneFlag(ShipmentStatus status) {
        milestoneFlags |= (1 << status.ordinal());
    }

    /**
     * Check if milestone reached
     */
    public boolean hasMilestone(ShipmentStatus status) {
        return (milestoneFlags & (1 << status.ordinal())) != 0;
    }

    private static String generateShipmentNumber() {
        return "SHP-" + System.currentTimeMillis();
    }

    /**
     * Tracking Event - Immutable for thread safety
     */
    @Getter
    public static class TrackingEvent {
        private final Instant timestamp;
        private final String description;
        private final String status;
        private final String location;

        public TrackingEvent(Instant timestamp, String description, String status, String location) {
            this.timestamp = timestamp;
            this.description = description;
            this.status = status;
            this.location = location;
        }
    }
}


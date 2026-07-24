package com.tradesphere.ai.shipment.adapter.out.persistence;

import com.tradesphere.ai.shipment.domain.model.ShipmentStatus;
import com.tradesphere.ai.shipment.domain.model.TransportMode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * JPA Entity for Shipment persistence
 * Optimized for high-throughput tracking operations
 */
@Entity
@Table(name = "shipments", indexes = {
    @Index(name = "idx_shipment_number", columnList = "shipment_number"),
    @Index(name = "idx_shipment_tenant", columnList = "tenant_id"),
    @Index(name = "idx_shipment_status", columnList = "status, tenant_id"),
    @Index(name = "idx_shipment_reference", columnList = "reference_number, tenant_id"),
    @Index(name = "idx_shipment_eta", columnList = "estimated_arrival, status")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentJpaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "shipment_number", nullable = false, unique = true)
    private String shipmentNumber;

    @Column(name = "reference_number")
    private String referenceNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ShipmentStatus status;

    @Column(name = "estimated_arrival")
    private Instant estimatedArrival;

    @Column(name = "actual_arrival")
    private Instant actualArrival;

    // Route
    @Column(name = "origin_port")
    private String originPort;

    @Column(name = "origin_country")
    private String originCountry;

    @Column(name = "destination_port")
    private String destinationPort;

    @Column(name = "destination_country")
    private String destinationCountry;

    // Transport
    @Enumerated(EnumType.STRING)
    @Column(name = "transport_mode")
    private TransportMode transportMode;

    @Column(name = "vessel_name")
    private String vesselName;

    @Column(name = "voyage_number")
    private String voyageNumber;

    @Column(name = "container_number")
    private String containerNumber;

    // Parties
    @Column(name = "shipper_id")
    private UUID shipperId;

    @Column(name = "consignee_id")
    private UUID consigneeId;

    @Column(name = "carrier_id")
    private UUID carrierId;

    // Documents
    @Column(name = "bill_of_lading_number")
    private String billOfLadingNumber;

    @Column(name = "documents_received")
    private boolean documentsReceived;

    // Milestones
    @Column(name = "milestone_flags")
    private Byte milestoneFlags;

    // Tracking events (stored as JSON or separate table)
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "shipment_tracking_events", joinColumns = @JoinColumn(name = "shipment_id"))
    @Builder.Default
    private List<TrackingEventEntity> trackingEvents = new ArrayList<>();

    // Audit
    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Version
    @Column(name = "version")
    private Long version;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrackingEventEntity {
        @Column(name = "event_timestamp")
        private Instant timestamp;

        @Column(name = "description", columnDefinition = "TEXT")
        private String description;

        @Column(name = "event_status")
        private String status;

        @Column(name = "location")
        private String location;
    }
}


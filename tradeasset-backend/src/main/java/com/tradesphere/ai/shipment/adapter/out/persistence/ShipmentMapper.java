package com.tradesphere.ai.shipment.adapter.out.persistence;

import com.tradesphere.ai.shipment.domain.model.Shipment;
import com.tradesphere.ai.shipment.domain.model.ShipmentId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper between Shipment domain model and JPA entity
 * Optimized for minimal object creation
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ShipmentMapper {

    /**
     * Convert domain model to JPA entity
     */
    public ShipmentJpaEntity toEntity(Shipment shipment) {
        // Map tracking events
        List<ShipmentJpaEntity.TrackingEventEntity> eventEntities = shipment.getTrackingEvents().stream()
                .map(event -> new ShipmentJpaEntity.TrackingEventEntity(
                    event.getTimestamp(),
                    event.getDescription(),
                    event.getStatus(),
                    event.getLocation()
                ))
                .collect(Collectors.toList());

        return ShipmentJpaEntity.builder()
                .id(shipment.getId().value())
                .tenantId(shipment.getTenantId())
                .shipmentNumber(shipment.getShipmentNumber())
                .referenceNumber(shipment.getReferenceNumber())
                .status(shipment.getStatus())
                .estimatedArrival(shipment.getEstimatedArrival())
                .actualArrival(shipment.getActualArrival())
                .originPort(shipment.getOriginPort())
                .originCountry(shipment.getOriginCountry())
                .destinationPort(shipment.getDestinationPort())
                .destinationCountry(shipment.getDestinationCountry())
                .transportMode(shipment.getTransportMode())
                .vesselName(shipment.getVesselName())
                .voyageNumber(shipment.getVoyageNumber())
                .containerNumber(shipment.getContainerNumber())
                .shipperId(shipment.getShipperId())
                .consigneeId(shipment.getConsigneeId())
                .carrierId(shipment.getCarrierId())
                .billOfLadingNumber(shipment.getBillOfLadingNumber())
                .documentsReceived(shipment.isDocumentsReceived())
                .milestoneFlags(shipment.getMilestoneFlags())
                .trackingEvents(eventEntities)
                .createdBy(shipment.getCreatedBy())
                .createdAt(shipment.getCreatedAt())
                .updatedBy(shipment.getUpdatedBy())
                .updatedAt(shipment.getUpdatedAt())
                .version(shipment.getVersion())
                .build();
    }

    /**
     * Convert JPA entity to domain model
     */
    public Shipment toDomain(ShipmentJpaEntity entity) {
        // Reconstruct shipment using create method
        Shipment shipment = Shipment.create(
            entity.getTenantId(),
            entity.getReferenceNumber(),
            entity.getOriginPort(),
            entity.getDestinationPort(),
            entity.getTransportMode(),
            entity.getEstimatedArrival(),
            entity.getCreatedBy()
        );

        // Set additional fields via reflection or setter access
        setField(shipment, "id", ShipmentId.of(entity.getId()));
        setField(shipment, "shipmentNumber", entity.getShipmentNumber());
        setField(shipment, "status", entity.getStatus());
        setField(shipment, "actualArrival", entity.getActualArrival());
        setField(shipment, "originCountry", entity.getOriginCountry());
        setField(shipment, "destinationCountry", entity.getDestinationCountry());
        setField(shipment, "vesselName", entity.getVesselName());
        setField(shipment, "voyageNumber", entity.getVoyageNumber());
        setField(shipment, "containerNumber", entity.getContainerNumber());
        setField(shipment, "shipperId", entity.getShipperId());
        setField(shipment, "consigneeId", entity.getConsigneeId());
        setField(shipment, "carrierId", entity.getCarrierId());
        setField(shipment, "billOfLadingNumber", entity.getBillOfLadingNumber());
        setField(shipment, "documentsReceived", entity.isDocumentsReceived());
        setField(shipment, "milestoneFlags", entity.getMilestoneFlags());
        
        // Reconstruct tracking events
        List<Shipment.TrackingEvent> domainEvents = entity.getTrackingEvents().stream()
                .map(e -> new Shipment.TrackingEvent(
                    e.getTimestamp(),
                    e.getDescription(),
                    e.getStatus(),
                    e.getLocation()
                ))
                .collect(Collectors.toList());
        setField(shipment, "trackingEvents", domainEvents);
        
        setField(shipment, "createdAt", entity.getCreatedAt());
        setField(shipment, "updatedBy", entity.getUpdatedBy());
        setField(shipment, "updatedAt", entity.getUpdatedAt());
        setField(shipment, "version", entity.getVersion());

        return shipment;
    }

    /**
     * Helper method to set private fields via reflection
     */
    private void setField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException e) {
            // Try superclass
            try {
                var field = target.getClass().getSuperclass().getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(target, value);
            } catch (Exception ex) {
                log.warn("Could not set field {}: {}", fieldName, ex.getMessage());
            }
        } catch (Exception e) {
            log.warn("Could not set field {}: {}", fieldName, e.getMessage());
        }
    }
}


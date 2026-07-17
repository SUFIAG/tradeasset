package com.tradesphere.ai.shipment.application.port.out;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Port for external tracking providers (FedEx, DHL, Maersk, UPS, etc.)
 */
public interface ExternalTrackingProvider {

    /**
     * Get provider name
     */
    String getProviderName();

    /**
     * Track a shipment by tracking number
     */
    TrackingResponse trackShipment(String trackingNumber);

    /**
     * Get full tracking history
     */
    List<TrackingEvent> getTrackingHistory(String trackingNumber);

    /**
     * Get estimated delivery time
     */
    EstimatedDelivery getEstimatedDelivery(String trackingNumber);

    /**
     * Validate tracking number format
     */
    boolean isTrackingNumberValid(String trackingNumber);

    /**
     * Get current shipment status
     */
    ShipmentStatus getCurrentStatus(String trackingNumber);

    // DTOs
    record TrackingResponse(
        boolean success,
        String trackingNumber,
        ShipmentStatus currentStatus,
        List<TrackingEvent> trackingEvents,
        EstimatedDelivery estimatedDelivery,
        String errorMessage
    ) {
        public static TrackingResponse error(String message) {
            return new TrackingResponse(false, null, null, null, null, message);
        }
    }

    record TrackingEvent(
        LocalDateTime timestamp,
        String description,
        String location,
        String statusCode
    ) {}

    record EstimatedDelivery(
        LocalDateTime estimatedDate,
        String deliveryLocation
    ) {}

    enum ShipmentStatus {
        CREATED,
        PICKED_UP,
        IN_TRANSIT,
        ARRIVED_AT_PORT,
        CUSTOMS_CLEARANCE,
        OUT_FOR_DELIVERY,
        DELIVERED,
        DELIVERY_FAILED,
        RETURNED,
        CANCELLED
    }
}


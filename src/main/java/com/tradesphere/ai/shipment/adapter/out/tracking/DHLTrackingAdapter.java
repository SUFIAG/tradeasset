package com.tradesphere.ai.shipment.adapter.out.tracking;

import com.tradesphere.ai.shipment.application.port.out.ExternalTrackingProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * DHL Tracking API Integration
 * Implements real-time tracking integration with DHL
 */
@Component
@Slf4j
public class DHLTrackingAdapter implements ExternalTrackingProvider {

    private static final String API_BASE_URL = "https://api-eu.dhl.com/track/shipments";
    private final RestTemplate restTemplate;
    private final String apiKey;

    public DHLTrackingAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.apiKey = System.getenv("DHL_API_KEY"); // From environment
    }

    @Override
    public String getProviderName() {
        return "DHL";
    }

    @Override
    public TrackingResponse trackShipment(String trackingNumber) {
        log.info("Tracking DHL shipment: {}", trackingNumber);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("DHL-API-Key", apiKey);
            headers.set("Accept", "application/json");

            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(
                API_BASE_URL + "?trackingNumber=" + trackingNumber,
                HttpMethod.GET,
                entity,
                Map.class
            );

            return parseResponse(response.getBody());

        } catch (Exception e) {
            log.error("Failed to track DHL shipment: {}", trackingNumber, e);
            return TrackingResponse.error("Failed to track shipment: " + e.getMessage());
        }
    }

    @Override
    public List<TrackingEvent> getTrackingHistory(String trackingNumber) {
        TrackingResponse response = trackShipment(trackingNumber);
        return response.trackingEvents() != null ? response.trackingEvents() : new ArrayList<>();
    }

    @Override
    public EstimatedDelivery getEstimatedDelivery(String trackingNumber) {
        TrackingResponse response = trackShipment(trackingNumber);
        return response.estimatedDelivery();
    }

    @Override
    public boolean isTrackingNumberValid(String trackingNumber) {
        // DHL tracking numbers are 10 digits
        return trackingNumber != null && trackingNumber.matches("\\d{10}");
    }

    @Override
    public ShipmentStatus getCurrentStatus(String trackingNumber) {
        TrackingResponse response = trackShipment(trackingNumber);
        return response.currentStatus();
    }

    private TrackingResponse parseResponse(Map<String, Object> body) {
        if (body == null) {
            return TrackingResponse.error("Empty response from DHL");
        }

        try {
            List<Map<String, Object>> shipments = 
                (List<Map<String, Object>>) body.get("shipments");
            
            if (shipments == null || shipments.isEmpty()) {
                return TrackingResponse.error("No tracking information found");
            }

            Map<String, Object> shipment = shipments.get(0);
            List<Map<String, Object>> events = 
                (List<Map<String, Object>>) shipment.get("events");

            List<TrackingEvent> trackingEvents = new ArrayList<>();
            if (events != null) {
                for (Map<String, Object> event : events) {
                    trackingEvents.add(new TrackingEvent(
                        LocalDateTime.parse((String) event.get("timestamp")),
                        (String) event.get("description"),
                        (String) event.get("location"),
                        (String) event.get("statusCode")
                    ));
                }
            }

            Map<String, Object> estimatedDeliveryInfo = 
                (Map<String, Object>) shipment.get("estimatedTimeOfDelivery");
            
            EstimatedDelivery estimated = null;
            if (estimatedDeliveryInfo != null) {
                estimated = new EstimatedDelivery(
                    LocalDateTime.parse((String) estimatedDeliveryInfo.get("dateTime")),
                    (String) shipment.get("destination")
                );
            }

            return new TrackingResponse(
                true,
                (String) shipment.get("id"),
                mapStatus((String) shipment.get("status")),
                trackingEvents,
                estimated,
                null
            );

        } catch (Exception e) {
            log.error("Failed to parse DHL response", e);
            return TrackingResponse.error("Failed to parse tracking response");
        }
    }

    private ShipmentStatus mapStatus(String dhlStatus) {
        return switch (dhlStatus.toUpperCase()) {
            case "TRANSIT" -> ShipmentStatus.IN_TRANSIT;
            case "DELIVERED" -> ShipmentStatus.DELIVERED;
            case "WITH_DELIVERY_COURIER" -> ShipmentStatus.OUT_FOR_DELIVERY;
            case "FAILURE" -> ShipmentStatus.DELIVERY_FAILED;
            default -> ShipmentStatus.IN_TRANSIT;
        };
    }
}


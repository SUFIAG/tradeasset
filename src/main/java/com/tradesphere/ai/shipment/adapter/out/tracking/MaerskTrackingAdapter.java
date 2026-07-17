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
 * Maersk Line Tracking API Integration
 * For sea freight / container tracking
 */
@Component
@Slf4j
public class MaerskTrackingAdapter implements ExternalTrackingProvider {

    private static final String API_BASE_URL = "https://api.maersk.com/track-and-trace";
    private final RestTemplate restTemplate;
    private final String apiKey;

    public MaerskTrackingAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.apiKey = System.getenv("MAERSK_API_KEY"); // From environment
    }

    @Override
    public String getProviderName() {
        return "Maersk";
    }

    @Override
    public TrackingResponse trackShipment(String trackingNumber) {
        log.info("Tracking Maersk container: {}", trackingNumber);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Consumer-Key", apiKey);
            headers.set("Accept", "application/json");

            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(
                API_BASE_URL + "/shipments/" + trackingNumber,
                HttpMethod.GET,
                entity,
                Map.class
            );

            return parseResponse(response.getBody());

        } catch (Exception e) {
            log.error("Failed to track Maersk container: {}", trackingNumber, e);
            return TrackingResponse.error("Failed to track container: " + e.getMessage());
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
        // Container numbers are 11 characters (4 letters + 7 digits)
        return trackingNumber != null && trackingNumber.matches("[A-Z]{4}\\d{7}");
    }

    @Override
    public ShipmentStatus getCurrentStatus(String trackingNumber) {
        TrackingResponse response = trackShipment(trackingNumber);
        return response.currentStatus();
    }

    private TrackingResponse parseResponse(Map<String, Object> body) {
        if (body == null) {
            return TrackingResponse.error("Empty response from Maersk");
        }

        try {
            List<Map<String, Object>> containers = 
                (List<Map<String, Object>>) body.get("containers");
            
            if (containers == null || containers.isEmpty()) {
                return TrackingResponse.error("No tracking information found");
            }

            Map<String, Object> container = containers.get(0);
            List<Map<String, Object>> events = 
                (List<Map<String, Object>>) container.get("events");

            List<TrackingEvent> trackingEvents = new ArrayList<>();
            if (events != null) {
                for (Map<String, Object> event : events) {
                    trackingEvents.add(new TrackingEvent(
                        LocalDateTime.parse((String) event.get("eventDateTime")),
                        (String) event.get("eventTypeCode") + ": " + (String) event.get("transportEventTypeCode"),
                        (String) event.get("location"),
                        (String) event.get("emptyIndicatorCode")
                    ));
                }
            }

            Map<String, Object> estimatedInfo = 
                (Map<String, Object>)container.get("estimatedArrival");
            
            EstimatedDelivery estimated = null;
            if (estimatedInfo != null) {
                estimated = new EstimatedDelivery(
                    LocalDateTime.parse((String) estimatedInfo.get("dateTime")),
                    (String) container.get("destinationPort")
                );
            }

            return new TrackingResponse(
                true,
                (String) container.get("containerNumber"),
                mapStatus((String) container.get("status")),
                trackingEvents,
                estimated,
                null
            );

        } catch (Exception e) {
            log.error("Failed to parse Maersk response", e);
            return TrackingResponse.error("Failed to parse tracking response");
        }
    }

    private ShipmentStatus mapStatus(String maerskStatus) {
        return switch (maerskStatus.toUpperCase()) {
            case "IN_TRANSIT" -> ShipmentStatus.IN_TRANSIT;
            case "ARRIVED" -> ShipmentStatus.ARRIVED_AT_PORT;
            case "DELIVERED" -> ShipmentStatus.DELIVERED;
            case "GATE_OUT" -> ShipmentStatus.IN_TRANSIT;
            default -> ShipmentStatus.IN_TRANSIT;
        };
    }
}


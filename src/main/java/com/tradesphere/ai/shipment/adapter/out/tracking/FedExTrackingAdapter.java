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
 * FedEx Tracking API Integration
 * Implements real-time tracking integration with FedEx
 */
@Component
@Slf4j
public class FedExTrackingAdapter implements ExternalTrackingProvider {

    private static final String API_BASE_URL = "https://apis.fedex.com/track/v1";
    private final RestTemplate restTemplate;
    private final String apiKey;

    public FedExTrackingAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.apiKey = System.getenv("FEDEX_API_KEY"); // From environment
    }

    @Override
    public String getProviderName() {
        return "FedEx";
    }

    @Override
    public TrackingResponse trackShipment(String trackingNumber) {
        log.info("Tracking FedEx shipment: {}", trackingNumber);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);
            headers.set("Content-Type", "application/json");

            Map<String, Object> request = Map.of(
                "trackingInfo", List.of(Map.of(
                    "trackingNumberInfo", Map.of(
                        "trackingNumber", trackingNumber
                    )
                ))
            );

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
            ResponseEntity<Map> response = restTemplate.exchange(
                API_BASE_URL + "/trackingnumbers",
                HttpMethod.POST,
                entity,
                Map.class
            );

            return parseResponse(response.getBody());

        } catch (Exception e) {
            log.error("Failed to track FedEx shipment: {}", trackingNumber, e);
            return TrackingResponse.error("Failed to track shipment: " + e.getMessage());
        }
    }

    @Override
    public List<TrackingEvent> getTrackingHistory(String trackingNumber) {
        log.info("Getting tracking history from FedEx: {}", trackingNumber);

        TrackingResponse response = trackShipment(trackingNumber);
        return response.trackingEvents() != null ? response.trackingEvents() : new ArrayList<>();
    }

    @Override
    public EstimatedDelivery getEstimatedDelivery(String trackingNumber) {
        log.info("Getting estimated delivery from FedEx: {}", trackingNumber);

        TrackingResponse response = trackShipment(trackingNumber);
        return response.estimatedDelivery();
    }

    @Override
    public boolean isTrackingNumberValid(String trackingNumber) {
        // FedEx tracking numbers are 12 digits
        return trackingNumber != null && trackingNumber.matches("\\d{12}");
    }

    @Override
    public ShipmentStatus getCurrentStatus(String trackingNumber) {
        TrackingResponse response = trackShipment(trackingNumber);
        return response.currentStatus();
    }

    private TrackingResponse parseResponse(Map<String, Object> body) {
        if (body == null) {
            return TrackingResponse.error("Empty response from FedEx");
        }

        try {
            List<Map<String, Object>> trackingOutput =
                (List<Map<String, Object>>) body.get("output");

            if (trackingOutput == null || trackingOutput.isEmpty()) {
                return TrackingResponse.error("No tracking information found");
            }

            Map<String, Object> tracking = trackingOutput.get(0);
            List<Map<String, Object>> scanEvents =
                (List<Map<String, Object>>) tracking.get("scanEvents");

            List<TrackingEvent> events = new ArrayList<>();
            if (scanEvents != null) {
                for (Map<String, Object> scan : scanEvents) {
                    events.add(new TrackingEvent(
                        LocalDateTime.parse((String) scan.get("date")),
                        (String) scan.get("eventDescription"),
                        (String) scan.get("scanLocation"),
                        (String) scan.get("derivedStatus")
                    ));
                }
            }

            Map<String, Object> deliveryInfo =
                (Map<String, Object>) tracking.get("deliveryDetails");

            EstimatedDelivery estimated = null;
            if (deliveryInfo != null) {
                estimated = new EstimatedDelivery(
                    LocalDateTime.parse((String) deliveryInfo.get("estimatedDeliveryTimeWindow")),
                    (String) deliveryInfo.get("deliveryLocation")
                );
            }

            return new TrackingResponse(
                true,
                (String) tracking.get("trackingNumber"),
                ShipmentStatus.valueOf((String) tracking.get("status")),
                events,
                estimated,
                null
            );

        } catch (Exception e) {
            log.error("Failed to parse FedEx response", e);
            return TrackingResponse.error("Failed to parse tracking response");
        }
    }
}


package com.tradesphere.ai.document.adapter.out.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradesphere.ai.document.application.port.out.PublishDocumentEventPort;
import com.tradesphere.ai.shared.domain.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Kafka event publisher for document events
 * High-throughput async event publishing
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DocumentEventPublisher implements PublishDocumentEventPort {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String TOPIC = "document-events";

    @Override
    public void publish(DomainEvent event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC, event.getAggregateId(), eventJson);
            log.debug("Published event: {} for aggregate: {}",
                event.getClass().getSimpleName(), event.getAggregateId());
        } catch (Exception e) {
            log.error("Failed to publish event: {}", event.getClass().getSimpleName(), e);
            // Don't throw - event publishing should not fail the transaction
        }
    }
}


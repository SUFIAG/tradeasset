package com.tradesphere.ai.lc.adapter.out.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradesphere.ai.lc.application.port.out.PublishLCEventPort;
import com.tradesphere.ai.shared.domain.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * LC Event Publisher using Kafka
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LCEventPublisher implements PublishLCEventPort {

    private static final String TOPIC = "lc-events";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(DomainEvent event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC, event.getAggregateId(), eventJson);
            log.info("Published LC event: {} for aggregate: {}",
                    event.getClass().getSimpleName(), event.getAggregateId());
        } catch (Exception e) {
            log.error("Failed to publish LC event: {}", event, e);
            throw new RuntimeException("Failed to publish event", e);
        }
    }
}


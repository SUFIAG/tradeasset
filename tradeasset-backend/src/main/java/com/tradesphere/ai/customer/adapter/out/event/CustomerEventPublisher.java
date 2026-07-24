package com.tradesphere.ai.customer.adapter.out.event;

import com.tradesphere.ai.customer.application.port.out.PublishCustomerEventPort;
import com.tradesphere.ai.shared.domain.DomainEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Kafka adapter for publishing customer domain events
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerEventPublisher implements PublishCustomerEventPort {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String TOPIC_PREFIX = "customer.events.";

    @Override
    public void publish(DomainEvent event) {
        try {
            String topic = TOPIC_PREFIX + event.getEventType();
            String payload = objectMapper.writeValueAsString(event);

            kafkaTemplate.send(topic, event.getAggregateId(), payload)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to publish event {} to topic {}",
                                    event.getEventType(), topic, ex);
                        } else {
                            log.debug("Successfully published event {} to topic {}",
                                    event.getEventType(), topic);
                        }
                    });

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize event: {}", event.getEventType(), e);
            throw new RuntimeException("Failed to publish event", e);
        }
    }
}


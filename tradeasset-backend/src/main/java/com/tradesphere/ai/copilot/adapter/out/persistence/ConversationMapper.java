package com.tradesphere.ai.copilot.adapter.out.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradesphere.ai.copilot.domain.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Mapper between Conversation domain model and JPA entities
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ConversationMapper {

    private final ObjectMapper objectMapper;

    /**
     * Convert domain model to JPA entity
     */
    public ConversationJpaEntity toEntity(Conversation conversation) {
        ConversationJpaEntity entity = ConversationJpaEntity.builder()
                .id(conversation.getId().value())
                .userId(conversation.getUserId())
                .tenantId(conversation.getTenantId())
                .sessionId(conversation.getSessionId())
                .status(conversation.getStatus())
                .context(serializeContext(conversation.getContext()))
                .startedAt(conversation.getStartedAt())
                .lastMessageAt(conversation.getLastMessageAt())
                .endedAt(conversation.getEndedAt())
                .createdBy(conversation.getCreatedBy())
                .createdAt(conversation.getCreatedAt())
                .updatedBy(conversation.getUpdatedBy())
                .updatedAt(conversation.getUpdatedAt())
                .version(conversation.getVersion())
                .build();

        // Map messages
        List<MessageJpaEntity> messageEntities = conversation.getMessages().stream()
                .map(msg -> toMessageEntity(msg, entity))
                .collect(Collectors.toList());
        entity.setMessages(messageEntities);

        return entity;
    }

    /**
     * Convert JPA entity to domain model
     */
    public Conversation toDomain(ConversationJpaEntity entity) {
        // Build messages
        List<Message> messages = entity.getMessages().stream()
                .map(this::toMessageDomain)
                .collect(Collectors.toList());

        return Conversation.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .tenantId(entity.getTenantId())
                .sessionId(entity.getSessionId())
                .status(entity.getStatus())
                .messages(messages)
                .context(deserializeContext(entity.getContext()))
                .startedAt(entity.getStartedAt())
                .lastMessageAt(entity.getLastMessageAt())
                .endedAt(entity.getEndedAt())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedBy(entity.getUpdatedBy())
                .updatedAt(entity.getUpdatedAt())
                .version(entity.getVersion())
                .build();
    }

    private MessageJpaEntity toMessageEntity(Message message, ConversationJpaEntity conversation) {
        return MessageJpaEntity.builder()
                .id(message.getId())
                .conversation(conversation)
                .role(message.getRole())
                .content(message.getContent())
                .intent(message.getIntent())
                .confidence(message.getConfidence())
                .timestamp(message.getTimestamp())
                .build();
    }

    private Message toMessageDomain(MessageJpaEntity entity) {
        return Message.reconstruct(
                entity.getId(),
                entity.getRole(),
                entity.getContent(),
                entity.getIntent(),
                entity.getConfidence(),
                entity.getTimestamp()
        );
    }

    private Map<String, String> serializeContext(Map<String, Object> context) {
        Map<String, String> serialized = new HashMap<>();
        context.forEach((key, value) -> {
            try {
                serialized.put(key, objectMapper.writeValueAsString(value));
            } catch (JsonProcessingException e) {
                log.warn("Failed to serialize context value for key: {}", key, e);
                serialized.put(key, value.toString());
            }
        });
        return serialized;
    }

    private Map<String, Object> deserializeContext(Map<String, String> context) {
        Map<String, Object> deserialized = new HashMap<>();
        context.forEach((key, value) -> {
            try {
                deserialized.put(key, objectMapper.readValue(value, Object.class));
            } catch (JsonProcessingException e) {
                log.warn("Failed to deserialize context value for key: {}", key, e);
                deserialized.put(key, value);
            }
        });
        return deserialized;
    }
}


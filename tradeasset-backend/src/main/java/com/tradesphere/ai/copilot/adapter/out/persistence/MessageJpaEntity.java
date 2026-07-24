package com.tradesphere.ai.copilot.adapter.out.persistence;

import com.tradesphere.ai.copilot.domain.model.Intent;
import com.tradesphere.ai.copilot.domain.model.MessageRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * JPA Entity for Message persistence
 */
@Entity
@Table(name = "conversation_messages", indexes = {
    @Index(name = "idx_message_conversation", columnList = "conversation_id"),
    @Index(name = "idx_message_timestamp", columnList = "timestamp")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageJpaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private ConversationJpaEntity conversation;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private MessageRole role;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "intent")
    private Intent intent;

    @Column(name = "confidence")
    private Float confidence;

    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;
}


package com.tradesphere.ai.copilot.adapter.out.persistence;

import com.tradesphere.ai.copilot.domain.model.ConversationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * JPA Entity for Conversation persistence
 */
@Entity
@Table(name = "conversations", indexes = {
    @Index(name = "idx_conversation_session", columnList = "session_id"),
    @Index(name = "idx_conversation_user", columnList = "user_id"),
    @Index(name = "idx_conversation_status", columnList = "status")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationJpaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "session_id", nullable = false, unique = true)
    private String sessionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ConversationStatus status;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("timestamp ASC")
    @Builder.Default
    private List<MessageJpaEntity> messages = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "conversation_context", joinColumns = @JoinColumn(name = "conversation_id"))
    @MapKeyColumn(name = "context_key")
    @Column(name = "context_value", columnDefinition = "TEXT")
    @Builder.Default
    private Map<String, String> context = new HashMap<>();

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "last_message_at", nullable = false)
    private Instant lastMessageAt;

    @Column(name = "ended_at")
    private Instant endedAt;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Version
    @Column(name = "version")
    private Long version;
}


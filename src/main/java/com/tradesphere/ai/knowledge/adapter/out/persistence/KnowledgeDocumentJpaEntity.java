package com.tradesphere.ai.knowledge.adapter.out.persistence;

import com.tradesphere.ai.knowledge.domain.model.DocumentStatus;
import com.tradesphere.ai.knowledge.domain.model.DocumentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "knowledge_documents", schema = "knowledge")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeDocumentJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type")
    private DocumentType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentStatus status;

    private String category;

    @Column(name = "chunk_count")
    private Integer chunkCount;

    @Column(name = "embedding_model")
    private String embeddingModel;

    @Column(name = "indexed_at")
    private Instant indexedAt;

    @Column(name = "is_public")
    private boolean isPublic;

    @Column(name = "allowed_roles", columnDefinition = "TEXT[]")
    private String[] allowedRoles;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @Version
    private Long version;
}


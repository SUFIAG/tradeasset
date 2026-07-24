package com.tradesphere.ai.knowledge.domain.model;

import com.tradesphere.ai.shared.domain.AggregateRoot;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Knowledge Document Aggregate Root
 * Represents a document in the knowledge base
 */
@Getter
@Setter(AccessLevel.PRIVATE)
public class KnowledgeDocument extends AggregateRoot<KnowledgeDocumentId> {

    private UUID tenantId;
    private String title;
    private String content;
    private DocumentType type;
    private DocumentStatus status;
    private String category;
    private Map<String, String> metadata;

    // Embedding information
    private int chunkCount;
    private String embeddingModel;
    private Instant indexedAt;

    // Access control
    private boolean isPublic;
    private String[] allowedRoles;

    private KnowledgeDocument(KnowledgeDocumentId id) {
        super(id);
    }

    public static KnowledgeDocument create(
            UUID tenantId,
            String title,
            String content,
            DocumentType type,
            String category,
            UUID createdBy) {

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title is required");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Content is required");
        }

        KnowledgeDocument doc = new KnowledgeDocument(KnowledgeDocumentId.generate());
        doc.tenantId = tenantId;
        doc.title = title;
        doc.content = content;
        doc.type = type;
        doc.status = DocumentStatus.PENDING_INDEX;
        doc.category = category;
        doc.isPublic = false;
        doc.setCreatedBy(createdBy);
        doc.setCreatedAt(Instant.now());

        return doc;
    }

    public void index(int chunks, String model) {
        this.chunkCount = chunks;
        this.embeddingModel = model;
        this.indexedAt = Instant.now();
        this.status = DocumentStatus.INDEXED;
    }

    public void markPublic() {
        this.isPublic = true;
    }

    public void archive() {
        this.status = DocumentStatus.ARCHIVED;
    }
}


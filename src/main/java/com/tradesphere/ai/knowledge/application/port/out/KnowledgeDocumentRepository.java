package com.tradesphere.ai.knowledge.application.port.out;

import com.tradesphere.ai.knowledge.domain.model.KnowledgeDocument;
import com.tradesphere.ai.knowledge.domain.model.DocumentStatus;
import com.tradesphere.ai.knowledge.domain.model.DocumentType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for Knowledge Documents
 */
public interface KnowledgeDocumentRepository {

    KnowledgeDocument save(KnowledgeDocument document);

    Optional<KnowledgeDocument> findById(UUID id, UUID tenantId);

    List<KnowledgeDocument> findByTenantId(UUID tenantId);

    List<KnowledgeDocument> findByStatus(UUID tenantId, DocumentStatus status);

    List<KnowledgeDocument> findByType(UUID tenantId, DocumentType type);

    List<KnowledgeDocument> findByCategory(UUID tenantId, String category);

    void delete(UUID id);

    boolean exists(UUID id, UUID tenantId);
}


package com.tradesphere.ai.knowledge.adapter.out.persistence;

import com.tradesphere.ai.knowledge.domain.model.DocumentStatus;
import com.tradesphere.ai.knowledge.domain.model.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface KnowledgeDocumentJpaRepository extends JpaRepository<KnowledgeDocumentJpaEntity, UUID> {

    Optional<KnowledgeDocumentJpaEntity> findByIdAndTenantId(UUID id, UUID tenantId);

    List<KnowledgeDocumentJpaEntity> findByTenantIdOrderByCreatedAtDesc(UUID tenantId);

    List<KnowledgeDocumentJpaEntity> findByTenantIdAndStatus(UUID tenantId, DocumentStatus status);

    List<KnowledgeDocumentJpaEntity> findByTenantIdAndType(UUID tenantId, DocumentType type);

    List<KnowledgeDocumentJpaEntity> findByTenantIdAndCategory(UUID tenantId, String category);
}


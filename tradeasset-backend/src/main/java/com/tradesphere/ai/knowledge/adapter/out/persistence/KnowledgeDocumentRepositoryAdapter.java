package com.tradesphere.ai.knowledge.adapter.out.persistence;

import com.tradesphere.ai.knowledge.application.port.out.KnowledgeDocumentRepository;
import com.tradesphere.ai.knowledge.domain.model.DocumentStatus;
import com.tradesphere.ai.knowledge.domain.model.DocumentType;
import com.tradesphere.ai.knowledge.domain.model.KnowledgeDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class KnowledgeDocumentRepositoryAdapter implements KnowledgeDocumentRepository {

    private final KnowledgeDocumentJpaRepository jpaRepository;
    private final KnowledgeDocumentMapper mapper;

    @Override
    public KnowledgeDocument save(KnowledgeDocument document) {
        log.debug("Saving knowledge document: {}", document.getTitle());
        KnowledgeDocumentJpaEntity entity = mapper.toEntity(document);
        KnowledgeDocumentJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<KnowledgeDocument> findById(UUID id, UUID tenantId) {
        return jpaRepository.findByIdAndTenantId(id, tenantId)
            .map(mapper::toDomain);
    }

    @Override
    public List<KnowledgeDocument> findByTenantId(UUID tenantId) {
        return jpaRepository.findByTenantIdOrderByCreatedAtDesc(tenantId).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<KnowledgeDocument> findByStatus(UUID tenantId, DocumentStatus status) {
        return jpaRepository.findByTenantIdAndStatus(tenantId, status).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<KnowledgeDocument> findByType(UUID tenantId, DocumentType type) {
        return jpaRepository.findByTenantIdAndType(tenantId, type).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<KnowledgeDocument> findByCategory(UUID tenantId, String category) {
        return jpaRepository.findByTenantIdAndCategory(tenantId, category).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean exists(UUID id, UUID tenantId) {
        return jpaRepository.findByIdAndTenantId(id, tenantId).isPresent();
    }
}


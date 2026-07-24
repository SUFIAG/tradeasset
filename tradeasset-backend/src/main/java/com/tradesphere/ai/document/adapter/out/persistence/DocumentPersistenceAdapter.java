package com.tradesphere.ai.document.adapter.out.persistence;

import com.tradesphere.ai.document.application.port.out.ListDocumentsPort;
import com.tradesphere.ai.document.application.port.out.LoadDocumentPort;
import com.tradesphere.ai.document.application.port.out.SaveDocumentPort;
import com.tradesphere.ai.document.domain.model.Document;
import com.tradesphere.ai.document.domain.model.DocumentId;
import com.tradesphere.ai.document.domain.model.DocumentStatus;
import com.tradesphere.ai.shared.multitenancy.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Document persistence adapter
 * Implements repository ports with JPA
 * High-performance with tenant-aware queries
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DocumentPersistenceAdapter implements
        LoadDocumentPort,
        SaveDocumentPort,
        ListDocumentsPort {

    private final DocumentJpaRepository jpaRepository;
    private final DocumentMapper mapper;

    @Override
    public Optional<Document> loadDocumentById(DocumentId documentId) {
        UUID tenantId = TenantContext.getTenantId();
        return jpaRepository.findByIdAndTenantId(documentId.getValue(), tenantId)
            .map(mapper::toDomain);
    }

    @Override
    public Optional<Document> loadDocumentByStorageKey(String storageKey) {
        UUID tenantId = TenantContext.getTenantId();
        return jpaRepository.findByStorageKeyAndTenantId(storageKey, tenantId)
            .map(mapper::toDomain);
    }

    @Override
    public Document saveDocument(Document document) {
        DocumentJpaEntity entity = mapper.toJpaEntity(document);
        DocumentJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Page<Document> findAll(Pageable pageable) {
        UUID tenantId = TenantContext.getTenantId();
        return jpaRepository.findByTenantId(tenantId, pageable)
            .map(mapper::toDomain);
    }

    @Override
    public Page<Document> findByRelatedEntity(String entityType, String entityId, Pageable pageable) {
        UUID tenantId = TenantContext.getTenantId();
        return jpaRepository.findByTenantIdAndRelatedEntityTypeAndRelatedEntityId(
            tenantId, entityType, entityId, pageable)
            .map(mapper::toDomain);
    }

    @Override
    public Page<Document> findByStatus(DocumentStatus status, Pageable pageable) {
        UUID tenantId = TenantContext.getTenantId();
        return jpaRepository.findByTenantIdAndStatus(tenantId, status, pageable)
            .map(mapper::toDomain);
    }
}


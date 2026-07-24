package com.tradesphere.ai.knowledge.adapter.out.persistence;

import com.tradesphere.ai.knowledge.domain.model.KnowledgeDocument;
import com.tradesphere.ai.knowledge.domain.model.KnowledgeDocumentId;
import org.springframework.stereotype.Component;

@Component
public class KnowledgeDocumentMapper {

    public KnowledgeDocumentJpaEntity toEntity(KnowledgeDocument domain) {
        if (domain == null) return null;

        KnowledgeDocumentJpaEntity entity = new KnowledgeDocumentJpaEntity();
        entity.setId(domain.getId().value());
        entity.setTenantId(domain.getTenantId());
        entity.setTitle(domain.getTitle());
        entity.setContent(domain.getContent());
        entity.setType(domain.getType());
        entity.setStatus(domain.getStatus());
        entity.setCategory(domain.getCategory());
        entity.setChunkCount(domain.getChunkCount());
        entity.setEmbeddingModel(domain.getEmbeddingModel());
        entity.setIndexedAt(domain.getIndexedAt());
        entity.setPublic(domain.isPublic());
        entity.setAllowedRoles(domain.getAllowedRoles());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setCreatedBy(domain.getCreatedBy());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setUpdatedBy(domain.getUpdatedBy());
        entity.setVersion(domain.getVersion());

        return entity;
    }

    public KnowledgeDocument toDomain(KnowledgeDocumentJpaEntity entity) {
        if (entity == null) return null;

        KnowledgeDocument domain = KnowledgeDocument.create(
            entity.getTenantId(),
            entity.getTitle(),
            entity.getContent(),
            entity.getType(),
            entity.getCategory(),
            entity.getCreatedBy()
        );

        setField(domain, "id", KnowledgeDocumentId.of(entity.getId()));
        setField(domain, "status", entity.getStatus());
        setField(domain, "chunkCount", entity.getChunkCount());
        setField(domain, "embeddingModel", entity.getEmbeddingModel());
        setField(domain, "indexedAt", entity.getIndexedAt());
        setField(domain, "isPublic", entity.isPublic());
        setField(domain, "allowedRoles", entity.getAllowedRoles());
        setField(domain, "createdAt", entity.getCreatedAt());
        setField(domain, "updatedAt", entity.getUpdatedAt());
        setField(domain, "updatedBy", entity.getUpdatedBy());
        setField(domain, "version", entity.getVersion());

        return domain;
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            // Field not accessible, skip
        }
    }
}


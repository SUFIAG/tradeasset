package com.tradesphere.ai.collection.adapter.out.persistence;

import com.tradesphere.ai.collection.application.port.out.CollectionRepository;
import com.tradesphere.ai.collection.domain.model.CollectionId;
import com.tradesphere.ai.collection.domain.model.CollectionStatus;
import com.tradesphere.ai.collection.domain.model.DocumentaryCollection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adapter implementing CollectionRepository port
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CollectionRepositoryAdapter implements CollectionRepository {

    private final DocumentaryCollectionJpaRepository jpaRepository;
    private final CollectionMapper mapper;

    @Override
    @Transactional
    public DocumentaryCollection save(DocumentaryCollection collection) {
        log.debug("Saving documentary collection: {}", collection.getId());
        DocumentaryCollectionJpaEntity entity = mapper.toEntity(collection);
        DocumentaryCollectionJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DocumentaryCollection> findById(CollectionId id) {
        log.debug("Finding collection by ID: {}", id);
        return jpaRepository.findById(id.value())
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DocumentaryCollection> findByReference(String collectionRef, UUID tenantId) {
        log.debug("Finding collection by reference: {} for tenant: {}", collectionRef, tenantId);
        return jpaRepository.findByCollectionRefAndTenantId(collectionRef, tenantId)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentaryCollection> findByExporter(UUID exporterId, UUID tenantId) {
        log.debug("Finding collections by exporter: {} for tenant: {}", exporterId, tenantId);
        return jpaRepository.findByExporterIdAndTenantId(exporterId, tenantId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentaryCollection> findByImporter(UUID importerId, UUID tenantId) {
        log.debug("Finding collections by importer: {} for tenant: {}", importerId, tenantId);
        return jpaRepository.findByImporterIdAndTenantId(importerId, tenantId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentaryCollection> findByStatus(CollectionStatus status, UUID tenantId) {
        log.debug("Finding collections by status: {} for tenant: {}", status, tenantId);
        return jpaRepository.findByStatusAndTenantId(status, tenantId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentaryCollection> findByTenant(UUID tenantId) {
        log.debug("Finding all collections for tenant: {}", tenantId);
        return jpaRepository.findByTenantId(tenantId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(CollectionId id) {
        log.debug("Deleting collection: {}", id);
        jpaRepository.deleteById(id.value());
    }
}


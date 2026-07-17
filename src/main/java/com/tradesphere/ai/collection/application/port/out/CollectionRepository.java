package com.tradesphere.ai.collection.application.port.out;

import com.tradesphere.ai.collection.domain.model.CollectionId;
import com.tradesphere.ai.collection.domain.model.CollectionStatus;
import com.tradesphere.ai.collection.domain.model.DocumentaryCollection;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port for Documentary Collection persistence
 */
public interface CollectionRepository {
    DocumentaryCollection save(DocumentaryCollection collection);
    Optional<DocumentaryCollection> findById(CollectionId id);
    Optional<DocumentaryCollection> findByReference(String collectionRef, UUID tenantId);
    List<DocumentaryCollection> findByExporter(UUID exporterId, UUID tenantId);
    List<DocumentaryCollection> findByImporter(UUID importerId, UUID tenantId);
    List<DocumentaryCollection> findByStatus(CollectionStatus status, UUID tenantId);
    List<DocumentaryCollection> findByTenant(UUID tenantId);
    void delete(CollectionId id);
}


package com.tradesphere.ai.collection.adapter.out.persistence;

import com.tradesphere.ai.collection.domain.model.CollectionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentaryCollectionJpaRepository extends JpaRepository<DocumentaryCollectionJpaEntity, UUID> {
    
    Optional<DocumentaryCollectionJpaEntity> findByIdAndTenantId(UUID id, UUID tenantId);

    Optional<DocumentaryCollectionJpaEntity> findByCollectionRefAndTenantId(String collectionRef, UUID tenantId);

    List<DocumentaryCollectionJpaEntity> findByTenantId(UUID tenantId);

    List<DocumentaryCollectionJpaEntity> findByTenantIdOrderByCreatedAtDesc(UUID tenantId);

    List<DocumentaryCollectionJpaEntity> findByStatusAndTenantId(CollectionStatus status, UUID tenantId);

    List<DocumentaryCollectionJpaEntity> findByExporterIdAndTenantId(UUID exporterId, UUID tenantId);

    List<DocumentaryCollectionJpaEntity> findByImporterIdAndTenantId(UUID importerId, UUID tenantId);
}




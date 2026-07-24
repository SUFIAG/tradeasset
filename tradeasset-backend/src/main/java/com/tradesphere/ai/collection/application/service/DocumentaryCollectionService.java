package com.tradesphere.ai.collection.application.service;

import com.tradesphere.ai.collection.application.port.out.CollectionRepository;
import com.tradesphere.ai.collection.domain.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Documentary Collection Application Service
 * Responsibility: Orchestrate business operations and coordinate domain objects
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DocumentaryCollectionService {

    private final CollectionRepository repository;

    public DocumentaryCollection createCollection(
            String tenantId,
            String referenceNumber,
            UUID drawerId,
            UUID draweeId,
            CollectionType collectionType,
            BigDecimal amount,
            String currency,
            PaymentTerms paymentTerms,
            String goodsDescription,
            String userId
    ) {
        log.info("Creating documentary collection: drawer={}, amount={}", drawerId, amount);

        DocumentaryCollection collection = DocumentaryCollection.create(
            UUID.fromString(tenantId),
            collectionType,
            drawerId,
            "Exporter",
            draweeId,
            "Importer",
            amount,
            currency,
            UUID.fromString(userId)
        );

        collection.setReferenceNumber(referenceNumber);
        collection.setGoodsDescription(goodsDescription);
        collection.setPaymentTerms(paymentTerms != null ? paymentTerms.name() : null);

        return repository.save(collection);
    }

    public DocumentaryCollection getCollection(UUID id, String tenantId) {
        return repository.findById(CollectionId.of(id))
            .orElseThrow(() -> new RuntimeException("Collection not found: " + id));
    }

    public List<DocumentaryCollection> listCollections(String tenantId) {
        return repository.findByTenant(UUID.fromString(tenantId));
    }

    public void submitCollection(UUID id, String tenantId, String userId) {
        DocumentaryCollection collection = getCollection(id, tenantId);
        collection.submitToBank("Remitting Bank", "Collecting Bank");
        repository.save(collection);
    }

    public void presentDocuments(UUID id, String tenantId, String userId) {
        DocumentaryCollection collection = getCollection(id, tenantId);
        collection.presentToImporter();
        repository.save(collection);
    }

    public void acceptCollection(UUID id, String tenantId, String userId) {
        DocumentaryCollection collection = getCollection(id, tenantId);
        collection.acceptDocuments(30);
        repository.save(collection);
    }

    public void processPayment(UUID id, String tenantId, BigDecimal amount, String userId) {
        DocumentaryCollection collection = getCollection(id, tenantId);
        collection.processPayment();
        repository.save(collection);
    }
}


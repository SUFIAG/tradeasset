package com.tradesphere.ai.collection.application.service;

import com.tradesphere.ai.collection.adapter.out.persistence.DocumentaryCollectionJpaEntity;
import com.tradesphere.ai.collection.adapter.out.persistence.DocumentaryCollectionJpaRepository;
import com.tradesphere.ai.collection.domain.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Documentary Collection Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DocumentaryCollectionService {

    private final DocumentaryCollectionJpaRepository repository;

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
            tenantId, referenceNumber, drawerId, draweeId, collectionType,
            amount, currency, paymentTerms, goodsDescription
        );

        DocumentaryCollectionJpaEntity entity = toEntity(collection);
        DocumentaryCollectionJpaEntity saved = repository.save(entity);

        return toDomain(saved);
    }

    public DocumentaryCollection getCollection(UUID id, String tenantId) {
        DocumentaryCollectionJpaEntity entity = repository.findByIdAndTenantId(id, tenantId)
            .orElseThrow(() -> new RuntimeException("Collection not found: " + id));
        return toDomain(entity);
    }

    public List<DocumentaryCollection> listCollections(String tenantId) {
        return repository.findByTenantIdOrderByCreatedAtDesc(tenantId).stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    public void submitCollection(UUID id, String tenantId, String userId) {
        DocumentaryCollection collection = getCollection(id, tenantId);
        collection.submit(userId);
        repository.save(toEntity(collection));
    }

    public void presentDocuments(UUID id, String tenantId, String userId) {
        DocumentaryCollection collection = getCollection(id, tenantId);
        collection.presentDocuments(userId);
        repository.save(toEntity(collection));
    }

    public void acceptCollection(UUID id, String tenantId, String userId) {
        DocumentaryCollection collection = getCollection(id, tenantId);
        collection.accept(userId);
        repository.save(toEntity(collection));
    }

    public void processPayment(UUID id, String tenantId, BigDecimal amount, String userId) {
        DocumentaryCollection collection = getCollection(id, tenantId);
        collection.processPayment(amount, userId);
        repository.save(toEntity(collection));
    }

    private DocumentaryCollectionJpaEntity toEntity(DocumentaryCollection domain) {
        DocumentaryCollectionJpaEntity entity = new DocumentaryCollectionJpaEntity();
        entity.setId(domain.getId());
        entity.setTenantId(domain.getTenantId());
        entity.setCollectionNumber(domain.getCollectionNumber());
        entity.setReferenceNumber(domain.getReferenceNumber());
        entity.setDrawerId(domain.getDrawerId());
        entity.setDraweeId(domain.getDraweeId());
        entity.setRemittingBank(domain.getRemittingBank());
        entity.setCollectingBank(domain.getCollectingBank());
        entity.setPresentingBank(domain.getPresentingBank());
        entity.setCollectionType(domain.getCollectionType());
        entity.setAmount(domain.getAmount());
        entity.setCurrency(domain.getCurrency());
        entity.setPaymentTerms(domain.getPaymentTerms());
        entity.setTenorDays(domain.getTenorDays());
        entity.setCollectionDate(domain.getCollectionDate());
        entity.setPresentationDate(domain.getPresentationDate());
        entity.setAcceptanceDate(domain.getAcceptanceDate());
        entity.setPaymentDueDate(domain.getPaymentDueDate());
        entity.setPaymentDate(domain.getPaymentDate());
        entity.setStatus(domain.getStatus());

        if (domain.getRequiredDocuments() != null) {
            entity.setRequiredDocuments(domain.getRequiredDocuments().toArray(new String[0]));
        }

        entity.setDocumentsReceived(domain.isDocumentsReceived());
        entity.setDocumentsComplete(domain.isDocumentsComplete());
        entity.setCollectionInstructions(domain.getCollectionInstructions());
        entity.setProtestInstructions(domain.getProtestInstructions());
        entity.setChargesInstructions(domain.getChargesInstructions());
        entity.setBankCharges(domain.getBankCharges());
        entity.setCollectionCharges(domain.getCollectionCharges());
        entity.setChargesForAccount(domain.getChargesForAccount());
        entity.setGoodsDescription(domain.getGoodsDescription());
        entity.setInvoiceNumber(domain.getInvoiceNumber());
        entity.setInvoiceDate(domain.getInvoiceDate());
        entity.setAmountReceived(domain.getAmountReceived());
        entity.setOutstandingAmount(domain.getOutstandingAmount());
        entity.setProtestRequired(domain.isProtestRequired());
        entity.setProtested(domain.isProtested());
        entity.setProtestDate(domain.getProtestDate());
        entity.setProtestReason(domain.getProtestReason());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setCreatedBy(domain.getCreatedBy());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setUpdatedBy(domain.getUpdatedBy());
        entity.setCompletedAt(domain.getCompletedAt());
        entity.setCompletedBy(domain.getCompletedBy());
        entity.setVersion(domain.getVersion());

        return entity;
    }

    private DocumentaryCollection toDomain(DocumentaryCollectionJpaEntity entity) {
        DocumentaryCollection domain = new DocumentaryCollection();
        domain.setId(entity.getId());
        domain.setTenantId(entity.getTenantId());
        domain.setCollectionNumber(entity.getCollectionNumber());
        domain.setReferenceNumber(entity.getReferenceNumber());
        domain.setDrawerId(entity.getDrawerId());
        domain.setDraweeId(entity.getDraweeId());
        domain.setRemittingBank(entity.getRemittingBank());
        domain.setCollectingBank(entity.getCollectingBank());
        domain.setPresentingBank(entity.getPresentingBank());
        domain.setCollectionType(entity.getCollectionType());
        domain.setAmount(entity.getAmount());
        domain.setCurrency(entity.getCurrency());
        domain.setPaymentTerms(entity.getPaymentTerms());
        domain.setTenorDays(entity.getTenorDays());
        domain.setCollectionDate(entity.getCollectionDate());
        domain.setPresentationDate(entity.getPresentationDate());
        domain.setAcceptanceDate(entity.getAcceptanceDate());
        domain.setPaymentDueDate(entity.getPaymentDueDate());
        domain.setPaymentDate(entity.getPaymentDate());
        domain.setStatus(entity.getStatus());

        if (entity.getRequiredDocuments() != null) {
            domain.setRequiredDocuments(Arrays.asList(entity.getRequiredDocuments()));
        }

        domain.setDocumentsReceived(entity.isDocumentsReceived());
        domain.setDocumentsComplete(entity.isDocumentsComplete());
        domain.setCollectionInstructions(entity.getCollectionInstructions());
        domain.setProtestInstructions(entity.getProtestInstructions());
        domain.setChargesInstructions(entity.getChargesInstructions());
        domain.setBankCharges(entity.getBankCharges());
        domain.setCollectionCharges(entity.getCollectionCharges());
        domain.setChargesForAccount(entity.getChargesForAccount());
        domain.setGoodsDescription(entity.getGoodsDescription());
        domain.setInvoiceNumber(entity.getInvoiceNumber());
        domain.setInvoiceDate(entity.getInvoiceDate());
        domain.setAmountReceived(entity.getAmountReceived());
        domain.setOutstandingAmount(entity.getOutstandingAmount());
        domain.setProtestRequired(entity.isProtestRequired());
        domain.setProtested(entity.isProtested());
        domain.setProtestDate(entity.getProtestDate());
        domain.setProtestReason(entity.getProtestReason());
        domain.setCreatedAt(entity.getCreatedAt());
        domain.setCreatedBy(entity.getCreatedBy());
        domain.setUpdatedAt(entity.getUpdatedAt());
        domain.setUpdatedBy(entity.getUpdatedBy());
        domain.setCompletedAt(entity.getCompletedAt());
        domain.setCompletedBy(entity.getCompletedBy());
        domain.setVersion(entity.getVersion());

        return domain;
    }
}


package com.tradesphere.ai.collection.adapter.in.rest;

import com.tradesphere.ai.collection.domain.model.DocumentaryCollection;
import com.tradesphere.ai.collection.domain.model.PaymentTerms;
import org.springframework.stereotype.Component;

/**
 * Maps between DocumentaryCollection domain objects and REST DTOs
 * Responsibility: REST layer data transformation
 */
@Component
public class DocumentaryCollectionDtoMapper {

    /**
     * Convert domain model to REST response DTO
     */
    public DocumentaryCollectionController.CollectionResponse toResponse(DocumentaryCollection collection) {
        if (collection == null) {
            return null;
        }

        return DocumentaryCollectionController.CollectionResponse.builder()
            .id(collection.getId() != null ? collection.getId().getValue() : null)
            .tenantId(collection.getTenantId() != null ? collection.getTenantId().toString() : null)
            .collectionNumber(collection.getCollectionNumber())
            .referenceNumber(collection.getReferenceNumber())
            .drawerId(collection.getDrawerId())
            .draweeId(collection.getDraweeId())
            .collectionType(collection.getCollectionType())
            .amount(collection.getAmount())
            .currency(collection.getCurrency())
            .paymentTerms(collection.getPaymentTerms() != null ? PaymentTerms.valueOf(collection.getPaymentTerms()) : null)
            .status(collection.getStatus())
            .collectionDate(collection.getCollectionDate() != null ? collection.getCollectionDate().atStartOfDay() : null)
            .presentationDate(collection.getPresentationDate() != null ? collection.getPresentationDate().atStartOfDay() : null)
            .paymentDueDate(collection.getPaymentDueDate() != null ? collection.getPaymentDueDate().toLocalDate() : null)
            .amountReceived(collection.getAmountReceived())
            .outstandingAmount(collection.getOutstandingAmount())
            .build();
    }
}


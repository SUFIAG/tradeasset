package com.tradesphere.ai.collection.adapter.out.persistence;

import com.tradesphere.ai.collection.domain.model.DocumentaryCollection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper between DocumentaryCollection domain model and JPA entity
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CollectionMapper {

    /**
     * Convert domain model to JPA entity
     */
    public DocumentaryCollectionJpaEntity toEntity(DocumentaryCollection collection) {
        DocumentaryCollectionJpaEntity entity = new DocumentaryCollectionJpaEntity();
        
        entity.setId(collection.getId().value());
        entity.setTenantId(collection.getTenantId());
        entity.setCollectionRef(collection.getCollectionRef());
        entity.setType(collection.getType());
        entity.setStatus(collection.getStatus());
        
        // Parties
        entity.setExporterId(collection.getExporterId());
        entity.setExporterName(collection.getExporterName());
        entity.setExporterAddress(collection.getExporterAddress());
        entity.setImporterId(collection.getImporterId());
        entity.setImporterName(collection.getImporterName());
        entity.setImporterAddress(collection.getImporterAddress());
        
        // Banks
        entity.setRemittingBankName(collection.getRemittingBankName());
        entity.setRemittingBankSwift(collection.getRemittingBankSwift());
        entity.setCollectingBankName(collection.getCollectingBankName());
        entity.setCollectingBankSwift(collection.getCollectingBankSwift());
        entity.setPresentingBankName(collection.getPresentingBankName());
        entity.setPresentingBankSwift(collection.getPresentingBankSwift());
        
        // Financial
        entity.setCollectionAmount(collection.getCollectionAmount());
        entity.setCurrency(collection.getCurrency());
        entity.setCommissionAmount(collection.getCommissionAmount());
        entity.setAcceptanceRequired(collection.isAcceptanceRequired());
        entity.setTenorDays(collection.getTenorDays());
        entity.setMaturityDate(collection.getMaturityDate());
        
        // Documents
        entity.setDocumentsReceived(collection.isDocumentsReceived());
        entity.setDocumentsReceivedDate(collection.getDocumentsReceivedDate());
        
        // Dates
        entity.setCollectionDate(collection.getCollectionDate());
        entity.setPresentationDate(collection.getPresentationDate());
        entity.setAcceptanceDate(collection.getAcceptanceDate());
        entity.setPaymentDate(collection.getPaymentDate());
        
        // Instructions
        entity.setCollectionInstructions(collection.getCollectionInstructions());
        entity.setProtestRequired(collection.isProtestRequired());
        entity.setChargesInstruction(collection.getChargesInstruction());
        
        // Trade
        entity.setInvoiceNumber(collection.getInvoiceNumber());
        entity.setShipmentDetails(collection.getShipmentDetails());
        entity.setGoodsDescription(collection.getGoodsDescription());
        
        // Workflow
        entity.setWorkflowId(collection.getWorkflowId());
        
        // Audit
        entity.setCreatedBy(collection.getCreatedBy());
        entity.setCreatedAt(collection.getCreatedAt());
        entity.setUpdatedBy(collection.getUpdatedBy());
        entity.setUpdatedAt(collection.getUpdatedAt());
        entity.setVersion(collection.getVersion());
        
        return entity;
    }

    /**
     * Convert JPA entity to domain model
     */
    public DocumentaryCollection toDomain(DocumentaryCollectionJpaEntity entity) {
        // Use builder to reconstruct the aggregate
        DocumentaryCollection collection = DocumentaryCollection.create(
            entity.getTenantId(),
            entity.getType(),
            entity.getExporterId(),
            entity.getExporterName(),
            entity.getImporterId(),
            entity.getImporterName(),
            entity.getCollectionAmount() != null ? entity.getCollectionAmount() : BigDecimal.ZERO,
            entity.getCurrency(),
            entity.getCreatedBy()
        );
        
        // Manually set the ID and other fields via reflection or setter access
        // Since we need to reconstruct the aggregate with its persisted state
        setField(collection, "id", com.tradesphere.ai.collection.domain.model.CollectionId.of(entity.getId()));
        setField(collection, "collectionRef", entity.getCollectionRef());
        setField(collection, "status", entity.getStatus());
        setField(collection, "exporterAddress", entity.getExporterAddress());
        setField(collection, "importerAddress", entity.getImporterAddress());
        setField(collection, "remittingBankName", entity.getRemittingBankName());
        setField(collection, "remittingBankSwift", entity.getRemittingBankSwift());
        setField(collection, "collectingBankName", entity.getCollectingBankName());
        setField(collection, "collectingBankSwift", entity.getCollectingBankSwift());
        setField(collection, "presentingBankName", entity.getPresentingBankName());
        setField(collection, "presentingBankSwift", entity.getPresentingBankSwift());
        setField(collection, "commissionAmount", entity.getCommissionAmount());
        setField(collection, "acceptanceRequired", entity.isAcceptanceRequired());
        setField(collection, "tenorDays", entity.getTenorDays());
        setField(collection, "maturityDate", entity.getMaturityDate());
        setField(collection, "documentsReceived", entity.isDocumentsReceived());
        setField(collection, "documentsReceivedDate", entity.getDocumentsReceivedDate());
        setField(collection, "collectionDate", entity.getCollectionDate());
        setField(collection, "presentationDate", entity.getPresentationDate());
        setField(collection, "acceptanceDate", entity.getAcceptanceDate());
        setField(collection, "paymentDate", entity.getPaymentDate());
        setField(collection, "collectionInstructions", entity.getCollectionInstructions());
        setField(collection, "protestRequired", entity.isProtestRequired());
        setField(collection, "chargesInstruction", entity.getChargesInstruction());
        setField(collection, "invoiceNumber", entity.getInvoiceNumber());
        setField(collection, "shipmentDetails", entity.getShipmentDetails());
        setField(collection, "goodsDescription", entity.getGoodsDescription());
        setField(collection, "workflowId", entity.getWorkflowId());
        setField(collection, "createdAt", entity.getCreatedAt());
        setField(collection, "updatedBy", entity.getUpdatedBy());
        setField(collection, "updatedAt", entity.getUpdatedAt());
        setField(collection, "version", entity.getVersion());
        
        return collection;
    }

    /**
     * Helper method to set private fields via reflection
     */
    private void setField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException e) {
            // Try superclass
            try {
                var field = target.getClass().getSuperclass().getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(target, value);
            } catch (Exception ex) {
                log.warn("Could not set field {}: {}", fieldName, ex.getMessage());
            }
        } catch (Exception e) {
            log.warn("Could not set field {}: {}", fieldName, e.getMessage());
        }
    }
}


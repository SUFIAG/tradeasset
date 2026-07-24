package com.tradesphere.ai.collection.adapter.out.persistence;

import com.tradesphere.ai.collection.domain.model.DocumentaryCollection;
import com.tradesphere.ai.collection.domain.model.CollectionId;
import com.tradesphere.ai.collection.domain.model.PaymentTerms;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.UUID;

/**
 * Reflection-based mapper for DocumentaryCollection entity-domain conversion
 * Responsibility: Convert between JPA entities and domain objects
 */
@Component
public class DocumentaryCollectionReflectionMapper {

    public DocumentaryCollectionJpaEntity toEntity(DocumentaryCollection domain) {
        if (domain == null) return null;

        DocumentaryCollectionJpaEntity entity = new DocumentaryCollectionJpaEntity();
        entity.setId(domain.getId() != null ? domain.getId().getValue() : null);
        entity.setTenantId(domain.getTenantId() != null ? domain.getTenantId().toString() : null);
        entity.setCollectionNumber(domain.getCollectionNumber());
        entity.setReferenceNumber(domain.getReferenceNumber());
        entity.setDrawerId(domain.getDrawerId() != null ? domain.getDrawerId() : domain.getExporterId());
        entity.setDraweeId(domain.getDraweeId() != null ? domain.getDraweeId() : domain.getImporterId());
        entity.setRemittingBank(domain.getRemittingBankName());
        entity.setCollectingBank(domain.getCollectingBankName());
        entity.setPresentingBank(domain.getPresentingBankName());
        entity.setCollectionType(domain.getCollectionType() != null ? domain.getCollectionType() : domain.getType());
        entity.setAmount(domain.getAmount() != null ? domain.getAmount() : domain.getCollectionAmount());
        entity.setCurrency(domain.getCurrency());
        // Convert String to PaymentTerms enum
        if (domain.getPaymentTerms() != null) {
            try {
                entity.setPaymentTerms(PaymentTerms.valueOf(domain.getPaymentTerms()));
            } catch (IllegalArgumentException e) {
                // Handle invalid enum value gracefully - default to AT_SIGHT
                entity.setPaymentTerms(PaymentTerms.AT_SIGHT);
            }
        }
        entity.setTenorDays(domain.getTenorDays());
        entity.setCollectionDate(domain.getCollectionDate() != null ?
            domain.getCollectionDate().atStartOfDay() : null);
        entity.setPresentationDate(domain.getPresentationDate() != null ?
            domain.getPresentationDate().atStartOfDay() : null);
        entity.setAcceptanceDate(domain.getAcceptanceDate() != null ?
            domain.getAcceptanceDate().atStartOfDay() : null);
        // Convert LocalDateTime to LocalDate
        entity.setPaymentDueDate(domain.getPaymentDueDate() != null ?
            domain.getPaymentDueDate().toLocalDate() : null);
        // PaymentDate is already LocalDate in domain
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
        // InvoiceDate is already LocalDate in domain
        entity.setInvoiceDate(domain.getInvoiceDate());
        entity.setAmountReceived(domain.getAmountReceived());
        entity.setOutstandingAmount(domain.getOutstandingAmount());
        entity.setProtestRequired(domain.isProtestRequired());
        entity.setProtested(domain.isProtested());
        entity.setProtestDate(domain.getProtestDate() != null ?
            domain.getProtestDate().atStartOfDay() : null);
        entity.setProtestReason(domain.getProtestReason());
        entity.setCreatedAt(domain.getCreatedAt() != null ?
            LocalDateTime.ofInstant(domain.getCreatedAt(), ZoneId.systemDefault()) : null);
        entity.setCreatedBy(domain.getCreatedBy() != null ? domain.getCreatedBy().toString() : null);
        entity.setUpdatedAt(domain.getUpdatedAt() != null ?
            LocalDateTime.ofInstant(domain.getUpdatedAt(), ZoneId.systemDefault()) : null);
        entity.setUpdatedBy(domain.getUpdatedBy() != null ? domain.getUpdatedBy().toString() : null);
        entity.setCompletedAt(domain.getCompletedAt());
        // Convert UUID to String
        entity.setCompletedBy(domain.getCompletedBy() != null ? domain.getCompletedBy().toString() : null);
        entity.setVersion(domain.getVersion() != null ? domain.getVersion().intValue() : 0);

        return entity;
    }

    public DocumentaryCollection toDomain(DocumentaryCollectionJpaEntity entity) {
        if (entity == null) return null;

        try {
            DocumentaryCollection domain = new DocumentaryCollection();

            // Use reflection for ID
            setField(domain, "id", new CollectionId(entity.getId()));

            // Use public setters
            domain.setTenantId(entity.getTenantId() != null ? UUID.fromString(entity.getTenantId()) : null);
            domain.setCollectionNumber(entity.getCollectionNumber());
            domain.setReferenceNumber(entity.getReferenceNumber());
            domain.setDrawerId(entity.getDrawerId());
            domain.setDraweeId(entity.getDraweeId());
            domain.setExporterId(entity.getDrawerId());
            domain.setImporterId(entity.getDraweeId());
            domain.setRemittingBankName(entity.getRemittingBank());
            domain.setCollectingBankName(entity.getCollectingBank());
            domain.setPresentingBankName(entity.getPresentingBank());
            domain.setCollectionType(entity.getCollectionType());
            domain.setType(entity.getCollectionType());
            domain.setAmount(entity.getAmount());
            domain.setCollectionAmount(entity.getAmount());
            domain.setCurrency(entity.getCurrency());
            // Convert PaymentTerms enum to String
            domain.setPaymentTerms(entity.getPaymentTerms() != null ? entity.getPaymentTerms().name() : null);
            domain.setTenorDays(entity.getTenorDays());
            domain.setCollectionDate(entity.getCollectionDate() != null ? entity.getCollectionDate().toLocalDate() : null);
            domain.setPresentationDate(entity.getPresentationDate() != null ? entity.getPresentationDate().toLocalDate() : null);
            domain.setAcceptanceDate(entity.getAcceptanceDate() != null ? entity.getAcceptanceDate().toLocalDate() : null);
            // Convert LocalDate to LocalDateTime
            domain.setPaymentDueDate(entity.getPaymentDueDate() != null ?
                entity.getPaymentDueDate().atStartOfDay() : null);
            // PaymentDate is already LocalDate in entity
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
            // InvoiceDate is already LocalDate in entity
            domain.setInvoiceDate(entity.getInvoiceDate());
            domain.setAmountReceived(entity.getAmountReceived());
            domain.setOutstandingAmount(entity.getOutstandingAmount());
            domain.setProtestRequired(entity.isProtestRequired());
            domain.setProtested(entity.isProtested());
            domain.setProtestDate(entity.getProtestDate() != null ? entity.getProtestDate().toLocalDate() : null);
            domain.setProtestReason(entity.getProtestReason());
            domain.setCreatedAt(entity.getCreatedAt() != null ?
                entity.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant() : null);
            domain.setCreatedBy(entity.getCreatedBy() != null ? UUID.fromString(entity.getCreatedBy()) : null);
            domain.setUpdatedAt(entity.getUpdatedAt() != null ?
                entity.getUpdatedAt().atZone(ZoneId.systemDefault()).toInstant() : null);
            domain.setUpdatedBy(entity.getUpdatedBy() != null ? UUID.fromString(entity.getUpdatedBy()) : null);
            domain.setCompletedAt(entity.getCompletedAt());
            // Convert String to UUID
            domain.setCompletedBy(entity.getCompletedBy() != null ? UUID.fromString(entity.getCompletedBy()) : null);
            domain.setVersion(entity.getVersion() != 0 ? (long) entity.getVersion() : null);

            return domain;
        } catch (Exception e) {
            throw new RuntimeException("Error mapping entity to domain: " + e.getMessage(), e);
        }
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = findField(target.getClass(), fieldName);
        if (field != null) {
            field.setAccessible(true);
            field.set(target, value);
        }
    }

    private Field findField(Class<?> clazz, String fieldName) {
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }
        return null;
    }
}


package com.tradesphere.ai.collection.adapter.out.persistence;

import com.tradesphere.ai.collection.domain.model.CollectionStatus;
import com.tradesphere.ai.collection.domain.model.CollectionType;
import com.tradesphere.ai.collection.domain.model.PaymentTerms;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DocumentaryCollectionJpaEntity
 * Responsibility: JPA persistence representation of DocumentaryCollection aggregate
 *
 * This is a persistence model, not a domain model. It's optimized for database storage.
 */
@Entity
@Table(name = "documentary_collections")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentaryCollectionJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "collection_number")
    private String collectionNumber;

    @Column(name = "reference_number", nullable = false)
    private String referenceNumber;

    @Column(name = "drawer_id", nullable = false)
    private UUID drawerId;

    @Column(name = "drawee_id", nullable = false)
    private UUID draweeId;

    @Column(name = "remitting_bank")
    private String remittingBank;

    @Column(name = "collecting_bank")
    private String collectingBank;

    @Column(name = "presenting_bank")
    private String presentingBank;

    @Enumerated(EnumType.STRING)
    @Column(name = "collection_type", nullable = false)
    private CollectionType collectionType;

    @Column(nullable = false, precision = 20, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_terms", nullable = false)
    private PaymentTerms paymentTerms;

    @Column(name = "tenor_days")
    private Integer tenorDays;

    @Column(name = "collection_date", nullable = false)
    private LocalDateTime collectionDate;

    @Column(name = "presentation_date")
    private LocalDateTime presentationDate;

    @Column(name = "acceptance_date")
    private LocalDateTime acceptanceDate;

    @Column(name = "payment_due_date")
    private LocalDate paymentDueDate;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CollectionStatus status;

    @Column(name = "required_documents", columnDefinition = "TEXT[]")
    private String[] requiredDocuments;

    @Column(name = "documents_received")
    private boolean documentsReceived;

    @Column(name = "documents_complete")
    private boolean documentsComplete;

    @Column(name = "collection_instructions", columnDefinition = "TEXT")
    private String collectionInstructions;

    @Column(name = "protest_instructions", columnDefinition = "TEXT")
    private String protestInstructions;

    @Column(name = "charges_instructions", columnDefinition = "TEXT")
    private String chargesInstructions;

    @Column(name = "bank_charges", precision = 10, scale = 2)
    private BigDecimal bankCharges;

    @Column(name = "collection_charges", precision = 10, scale = 2)
    private BigDecimal collectionCharges;

    @Column(name = "charges_for_account")
    private String chargesForAccount;

    @Column(name = "goods_description", columnDefinition = "TEXT")
    private String goodsDescription;

    @Column(name = "invoice_number")
    private String invoiceNumber;

    @Column(name = "invoice_date")
    private LocalDate invoiceDate;

    @Column(name = "amount_received", precision = 20, scale = 2)
    private BigDecimal amountReceived;

    @Column(name = "outstanding_amount", precision = 20, scale = 2)
    private BigDecimal outstandingAmount;

    @Column(name = "protest_required")
    private boolean protestRequired;

    private boolean protested;

    @Column(name = "protest_date")
    private LocalDateTime protestDate;

    @Column(name = "protest_reason", columnDefinition = "TEXT")
    private String protestReason;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "completed_by")
    private String completedBy;

    @Version
    private int version;
}


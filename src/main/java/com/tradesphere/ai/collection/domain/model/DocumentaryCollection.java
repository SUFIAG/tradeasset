package com.tradesphere.ai.collection.domain.model;

import com.tradesphere.ai.shared.domain.AggregateRoot;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Documentary Collection Aggregate Root
 * Responsibility: Manage documentary collections lifecycle and business rules
 *
 * Manages documentary collections (D/P and D/A) lifecycle:
 * - D/P (Documents against Payment) - Documents released only upon payment
 * - D/A (Documents against Acceptance) - Documents released upon acceptance of draft
 *
 * Enforces business invariants and state transitions
 */
@Getter
@Setter
public class DocumentaryCollection extends AggregateRoot<CollectionId> {

    // Basic Information
    private UUID tenantId;
    private String collectionRef;
    private String collectionNumber;  // Collection number
    private String referenceNumber;
    private CollectionType type; // D/P or D/A
    private CollectionType collectionType;  // Alias for type
    private CollectionStatus status;

    // Parties
    private UUID exporterId;
    private UUID drawerId;  // Alias for exporter
    private String exporterName;
    private String exporterAddress;

    private UUID importerId;
    private UUID draweeId;  // Alias for importer
    private String importerName;
    private String importerAddress;

    // Banks
    private String remittingBankName;    // Exporter's bank
    private String remittingBankSwift;
    private String collectingBankName;   // Importer's bank
    private String collectingBankSwift;
    private String presentingBankName;   // Bank presenting to importer
    private String presentingBankSwift;

    // Financial Details
    private BigDecimal collectionAmount;
    private BigDecimal amount;  // Alias for collectionAmount
    private String currency;
    private BigDecimal commissionAmount;
    private boolean acceptanceRequired;  // true for D/A
    private String paymentTerms;
    private BigDecimal amountReceived;
    private BigDecimal outstandingAmount;

    // Payment Terms (for D/A)
    private Integer tenorDays;  // Days after acceptance for payment
    private LocalDate maturityDate;
    private LocalDateTime paymentDueDate;

    // Documents
    private List<ShippingDocument> documents;
    private List<String> requiredDocuments;
    private boolean documentsReceived;
    private boolean documentsComplete;
    private LocalDateTime documentsReceivedDate;

    // Dates
    private LocalDate collectionDate;
    private LocalDate presentationDate;
    private LocalDate acceptanceDate;
    private LocalDate paymentDate;

    // Instructions
    private String collectionInstructions;
    private String protestInstructions;
    private String chargesInstructions;
    private boolean protestRequired;  // Protest if not paid/accepted
    private boolean protested;
    private LocalDate protestDate;
    private String protestReason;
    private String chargesForAccount;  // Who pays charges
    private BigDecimal bankCharges;
    private BigDecimal collectionCharges;

    // Trade Details
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private String shipmentDetails;
    private String goodsDescription;

    // Completion tracking
    private LocalDateTime completedAt;
    private UUID completedBy;

    // Workflow & Audit
    private UUID workflowId;

    // Package-private constructor for mapper
    DocumentaryCollection(CollectionId id) {
        super(id);
        this.documents = new ArrayList<>();
    }

    // Public no-arg constructor for JPA/Mapper
    public DocumentaryCollection() {
        super(null);
        this.documents = new ArrayList<>();
    }

    /**
     * Create new documentary collection
     */
    public static DocumentaryCollection create(
            UUID tenantId,
            CollectionType type,
            UUID exporterId,
            String exporterName,
            UUID importerId,
            String importerName,
            BigDecimal amount,
            String currency,
            UUID createdBy) {

        if (tenantId == null) throw new IllegalArgumentException("Tenant ID required");
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valid amount required");
        }

        DocumentaryCollection collection = new DocumentaryCollection(CollectionId.generate());
        collection.tenantId = tenantId;
        collection.collectionRef = generateCollectionRef(type);
        collection.type = type;
        collection.status = CollectionStatus.DRAFT;
        collection.exporterId = exporterId;
        collection.exporterName = exporterName;
        collection.importerId = importerId;
        collection.importerName = importerName;
        collection.collectionAmount = amount;
        collection.currency = currency;
        collection.acceptanceRequired = (type == CollectionType.DOCUMENTS_AGAINST_ACCEPTANCE);
        collection.setCreatedBy(createdBy);
        collection.setCreatedAt(java.time.Instant.now());
        collection.collectionDate = LocalDate.now();

        return collection;
    }

    /**
     * Add shipping document
     */
    public void addDocument(String documentType, String documentNumber, int copies) {
        ShippingDocument doc = new ShippingDocument(
            UUID.randomUUID(),
            documentType,
            documentNumber,
            copies
        );
        this.documents.add(doc);
    }

    /**
     * Submit to remitting bank
     */
    public void submitToBank(String remittingBankName, String collectingBankName) {
        if (status != CollectionStatus.DRAFT) {
            throw new IllegalStateException("Can only submit draft collections");
        }
        this.status = CollectionStatus.SUBMITTED;
        this.remittingBankName = remittingBankName;
        this.collectingBankName = collectingBankName;
    }

    /**
     * Receive documents at collecting bank
     */
    public void receiveDocuments() {
        if (status != CollectionStatus.IN_TRANSIT) {
            throw new IllegalStateException("Documents must be in transit");
        }
        this.status = CollectionStatus.DOCUMENTS_RECEIVED;
        this.documentsReceived = true;
        this.documentsReceivedDate = LocalDateTime.now();
    }

    /**
     * Present documents to importer
     */
    public void presentToImporter() {
        if (!documentsReceived) {
            throw new IllegalStateException("Documents must be received first");
        }
        this.status = CollectionStatus.PRESENTED;
        this.presentationDate = LocalDate.now();
    }

    /**
     * Accept documents (D/A only)
     */
    public void acceptDocuments(Integer tenorDays) {
        if (type != CollectionType.DOCUMENTS_AGAINST_ACCEPTANCE) {
            throw new IllegalStateException("Acceptance only for D/A collections");
        }
        if (status != CollectionStatus.PRESENTED) {
            throw new IllegalStateException("Documents must be presented first");
        }
        this.status = CollectionStatus.ACCEPTED;
        this.acceptanceDate = LocalDate.now();
        this.tenorDays = tenorDays;
        this.maturityDate = LocalDate.now().plusDays(tenorDays);
    }

    /**
     * Process payment
     */
    public void processPayment() {
        if (status != CollectionStatus.PRESENTED && status != CollectionStatus.ACCEPTED) {
            throw new IllegalStateException("Invalid status for payment");
        }
        this.status = CollectionStatus.PAID;
        this.paymentDate = LocalDate.now();
    }

    /**
     * Reject collection
     */
    public void reject(String reason) {
        this.status = CollectionStatus.REJECTED;
    }

    private static String generateCollectionRef(CollectionType type) {
        String prefix = (type == CollectionType.DOCUMENTS_AGAINST_PAYMENT) ? "DP" : "DA";
        return prefix + "-" + System.currentTimeMillis();
    }

    /**
     * Shipping Document nested entity
     */
    @Getter
    public static class ShippingDocument {
        private UUID id;
        private String documentType;  // Bill of Lading, Invoice, etc.
        private String documentNumber;
        private int copies;

        public ShippingDocument(UUID id, String documentType, String documentNumber, int copies) {
            this.id = id;
            this.documentType = documentType;
            this.documentNumber = documentNumber;
            this.copies = copies;
        }
    }
}


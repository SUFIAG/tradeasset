package com.tradesphere.ai.shared.application.port.out.customer;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Read-only projection of a customer for other modules.
 *
 * @param customerType IMPORTER, EXPORTER, BANK, OTHER (or a legacy value)
 * @param status       DRAFT, PENDING_APPROVAL, APPROVED, ACTIVE, REJECTED, SUSPENDED
 * @param kycStatus    PENDING, IN_PROGRESS, COMPLETED, EXPIRED, REJECTED
 * @param riskRating   LOW, MEDIUM, HIGH, CRITICAL or null when not yet rated
 * @param country      ISO 3166-1 alpha-2
 * @param groupCode    corporate group for group limits (REQ-RSK-001); may be null
 * @param swiftBic     BIC when the customer is a bank; may be null
 */
public record CustomerSnapshot(
        UUID id,
        UUID tenantId,
        String customerNumber,
        String name,
        String customerType,
        String status,
        String kycStatus,
        LocalDate kycExpiryDate,
        String complianceStatus,
        String riskRating,
        String country,
        String industry,
        String groupCode,
        String swiftBic) {

    public boolean isActive() {
        return "ACTIVE".equals(status);
    }
}

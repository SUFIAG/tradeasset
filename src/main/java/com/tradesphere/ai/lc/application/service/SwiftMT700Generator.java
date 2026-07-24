package com.tradesphere.ai.lc.application.service;

import com.tradesphere.ai.lc.domain.model.LetterOfCredit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

/**
 * SWIFT MT700 message generator for Letter of Credit
 * Generates SWIFT format messages for LC issuance (MT700) and amendments (MT707)
 * Optimized for high throughput with minimal memory allocation
 */
@Slf4j
@Service
public class SwiftMT700Generator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyMMdd");
    private static final String MESSAGE_TYPE_ISSUANCE = "MT700";
    private static final String MESSAGE_TYPE_AMENDMENT = "MT707";

    /**
     * Generate SWIFT MT700 message for LC issuance
     * High-performance implementation using StringBuilder for memory efficiency
     */
    public String generateMT700(LetterOfCredit lc) {
        log.debug("Generating MT700 for LC: {}", lc.getLcNumber().value());
        return generateSwiftMessage(lc, MESSAGE_TYPE_ISSUANCE);
    }

    /**
     * Generate SWIFT MT707 message for LC amendment
     * High-performance implementation using StringBuilder for memory efficiency
     */
    public String generateMT707(LetterOfCredit lc, String amendmentDetails) {
        log.debug("Generating MT707 amendment for LC: {}", lc.getLcNumber().value());

        StringBuilder swift = new StringBuilder(2048);

        // Message header
        swift.append("{1:F01").append("BANKUSXXXXX").append("0000000000}");
        swift.append("{2:I").append(MESSAGE_TYPE_AMENDMENT).append("BANKAEXXXXN}");
        swift.append("{3:{108:").append(lc.getId().asString()).append("}}");

        // Message body
        swift.append("{4:\n");

        // 20: Documentary Credit Number
        appendField(swift, "20", lc.getLcNumber().value());

        // 21: Reference to Pre-Advice (Optional)
        // 23: Reference to Pre-Advice

        // 30: Date of Amendment
        appendField(swift, "30", DATE_FORMATTER.format(java.time.LocalDate.now()));

        // 26E: Number of Amendment
        appendField(swift, "26E", "1"); // Would be tracked in actual implementation

        // 31C: Date of Issue of Original Credit
        if (lc.getIssueDate() != null) {
            appendField(swift, "31C", DATE_FORMATTER.format(lc.getIssueDate()));
        }

        // 31D: Date and Place of Expiry
        if (lc.getExpiryDate() != null) {
            appendField(swift, "31D", DATE_FORMATTER.format(lc.getExpiryDate()));
        }

        // 32B: Currency Code, Amount
        appendField(swift, "32B", lc.getCurrency() +
                lc.getLcAmount().getAmount().stripTrailingZeros().toPlainString());

        // 39C: Additional Amounts Covered

        // 41A/D: Available With Bank
        if (lc.getAdvisingBankId() != null) {
            appendField(swift, "41D", "BANK" + lc.getAdvisingBankId().toString().substring(0, 8).toUpperCase());
        }

        // 44C: Latest Date of Shipment
        if (lc.getLastShipmentDate() != null) {
            appendField(swift, "44C", DATE_FORMATTER.format(lc.getLastShipmentDate()));
        }

        // 79: Narrative (Amendment Details)
        if (amendmentDetails != null && !amendmentDetails.isEmpty()) {
            appendMultiLineField(swift, "79", "AMENDMENT DETAILS:", amendmentDetails);
        }

        // Message trailer
        swift.append("-}");
        swift.append("{5:{CHK:000000000000}}");

        log.debug("MT707 amendment generated successfully, length: {} chars", swift.length());
        return swift.toString();
    }

    /**
     * Generate SWIFT message (common logic for MT700 and MT707)
     */
    private String generateSwiftMessage(LetterOfCredit lc, String messageType) {
        StringBuilder swift = new StringBuilder(2048); // Pre-allocate capacity

        // Message header
        swift.append("{1:F01").append("BANKUSXXXXX").append("0000000000}"); // Basic Header
        swift.append("{2:I").append(messageType).append("BANKAEXXXXN}"); // Application Header
        swift.append("{3:{108:").append(lc.getId().asString()).append("}}"); // User Header

        // Message body
        swift.append("{4:\n");

        // 20: Documentary Credit Number
        appendField(swift, "20", lc.getLcNumber().value());

        // 31C: Date of Issue
        if (lc.getIssueDate() != null) {
            appendField(swift, "31C", DATE_FORMATTER.format(lc.getIssueDate()));
        }

        // 31D: Date and Place of Expiry
        if (lc.getExpiryDate() != null) {
            appendField(swift, "31D", DATE_FORMATTER.format(lc.getExpiryDate()) +
                    (lc.getPortOfDischarge() != null ? lc.getPortOfDischarge() : ""));
        }

        // 50: Applicant (Buyer/Importer)
        appendMultiLineField(swift, "50",
                lc.getApplicantName(),
                lc.getApplicantAddress());

        // 59: Beneficiary (Seller/Exporter)
        appendMultiLineField(swift, "59",
                lc.getBeneficiaryName(),
                lc.getBeneficiaryAddress());

        // 32B: Currency Code, Amount
        appendField(swift, "32B", lc.getCurrency() +
                lc.getLcAmount().getAmount().stripTrailingZeros().toPlainString());

        // 41A/D: Available With Bank
        if (lc.getAdvisingBankId() != null) {
            appendField(swift, "41D", "BANK" + lc.getAdvisingBankId().toString().substring(0, 8).toUpperCase());
        }

        // 42C: Drafts at
        appendField(swift, "42C", "SIGHT");

        // 42D: Drawee (typically issuing bank)
        appendField(swift, "42D", "ISSUING BANK");

        // 43P: Partial Shipments
        appendField(swift, "43P", lc.isPartialShipmentAllowed() ? "ALLOWED" : "NOT ALLOWED");

        // 43T: Transhipment
        appendField(swift, "43T", lc.isTransshipmentAllowed() ? "ALLOWED" : "NOT ALLOWED");

        // 44A: Port of Loading/Airport of Departure
        if (lc.getPortOfLoading() != null) {
            appendField(swift, "44A", lc.getPortOfLoading());
        }

        // 44B: Port of Discharge/Airport of Destination
        if (lc.getPortOfDischarge() != null) {
            appendField(swift, "44B", lc.getPortOfDischarge());
        }

        // 44C: Latest Date of Shipment
        if (lc.getLastShipmentDate() != null) {
            appendField(swift, "44C", DATE_FORMATTER.format(lc.getLastShipmentDate()));
        }

        // 44D: Shipment Period
        // 44E: Place of Delivery
        if (lc.getPlaceOfDestination() != null) {
            appendField(swift, "44E", lc.getPlaceOfDestination());
        }

        // 45A: Description of Goods
        if (lc.getGoodsDescription() != null) {
            appendMultiLineField(swift, "45A", lc.getGoodsDescription());
        }

        // 46A: Documents Required
        StringBuilder docsRequired = new StringBuilder();
        if (lc.isCommercialInvoiceRequired()) {
            docsRequired.append("+ COMMERCIAL INVOICE IN 3 COPIES\n");
        }
        if (lc.isBillOfLadingRequired()) {
            docsRequired.append("+ FULL SET OF CLEAN ON BOARD OCEAN BILLS OF LADING\n");
        }
        if (lc.isPackingListRequired()) {
            docsRequired.append("+ PACKING LIST IN 3 COPIES\n");
        }
        if (lc.isCertificateOfOriginRequired()) {
            docsRequired.append("+ CERTIFICATE OF ORIGIN\n");
        }
        if (lc.isInsuranceCertificateRequired()) {
            docsRequired.append("+ INSURANCE CERTIFICATE OR POLICY\n");
        }
        appendMultiLineField(swift, "46A", docsRequired.toString());

        // 47A: Special Instructions
        if (lc.getSpecialInstructions() != null) {
            appendMultiLineField(swift, "47A", lc.getSpecialInstructions());
        }

        // 71B: Charges
        appendField(swift, "71B", "ALL CHARGES OUTSIDE ISSUING BANK ARE FOR BENEFICIARY");

        // 48: Period for Presentation
        appendField(swift, "48", "DOCUMENTS MUST BE PRESENTED WITHIN 21 DAYS AFTER SHIPMENT DATE");

        // 49: Confirmation Instructions
        appendField(swift, "49", "WITHOUT");

        // Message trailer
        swift.append("-}");
        swift.append("{5:{CHK:000000000000}}"); // Trailer

        log.debug("MT700 generated successfully, length: {} chars", swift.length());
        return swift.toString();
    }

    /**
     * Append single-line field - memory efficient
     */
    private void appendField(StringBuilder sb, String tag, String value) {
        sb.append(":").append(tag).append(":").append(value).append("\n");
    }

    /**
     * Append multi-line field - memory efficient
     */
    private void appendMultiLineField(StringBuilder sb, String tag, String... lines) {
        sb.append(":").append(tag).append(":");
        for (String line : lines) {
            if (line != null && !line.isEmpty()) {
                sb.append(line).append("\n");
            }
        }
    }
}


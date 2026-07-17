package com.tradesphere.ai.lc.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

/**
 * Comprehensive unit tests for Letter of Credit domain model
 * Tests LC lifecycle, amendments, business rules
 */
@DisplayName("Letter of Credit Domain Model Tests")
class LetterOfCreditTest {

    private static final String TENANT_ID = "TEST_TENANT";
    private static final UUID CUSTOMER_ID = UUID.randomUUID();
    private static final UUID BENEFICIARY_ID = UUID.randomUUID();
    private static final BigDecimal AMOUNT = new BigDecimal("100000.00");
    private static final String CURRENCY = "USD";
    private static final LocalDate EXPIRY_DATE = LocalDate.now().plusMonths(6);

    @Nested
    @DisplayName("LC Creation Tests")
    class LCCreationTests {

        @Test
        @DisplayName("Should create LC in DRAFT status")
        void shouldCreateLCInDraftStatus() {
            // When
            LetterOfCredit lc = LetterOfCredit.create(
                TENANT_ID,
                CUSTOMER_ID,
                BENEFICIARY_ID,
                AMOUNT,
                CURRENCY,
                EXPIRY_DATE
            );

            // Then
            assertThat(lc).isNotNull();
            assertThat(lc.getId()).isNotNull();
            assertThat(lc.getTenantId()).isEqualTo(TENANT_ID);
            assertThat(lc.getApplicantId()).isEqualTo(CUSTOMER_ID);
            assertThat(lc.getBeneficiaryId()).isEqualTo(BENEFICIARY_ID);
            assertThat(lc.getAmount()).isEqualByComparingTo(AMOUNT);
            assertThat(lc.getCurrency()).isEqualTo(CURRENCY);
            assertThat(lc.getExpiryDate()).isEqualTo(EXPIRY_DATE);
            assertThat(lc.getStatus()).isEqualTo(LCStatus.DRAFT);
        }

        @Test
        @DisplayName("Should fail when amount is null")
        void shouldFailWhenAmountIsNull() {
            assertThatThrownBy(() ->
                LetterOfCredit.create(TENANT_ID, CUSTOMER_ID, BENEFICIARY_ID, null, CURRENCY, EXPIRY_DATE)
            ).isInstanceOf(IllegalArgumentException.class)
             .hasMessageContaining("Amount");
        }

        @Test
        @DisplayName("Should fail when amount is zero")
        void shouldFailWhenAmountIsZero() {
            assertThatThrownBy(() ->
                LetterOfCredit.create(TENANT_ID, CUSTOMER_ID, BENEFICIARY_ID, BigDecimal.ZERO, CURRENCY, EXPIRY_DATE)
            ).isInstanceOf(IllegalArgumentException.class)
             .hasMessageContaining("Amount");
        }

        @Test
        @DisplayName("Should fail when amount is negative")
        void shouldFailWhenAmountIsNegative() {
            assertThatThrownBy(() ->
                LetterOfCredit.create(TENANT_ID, CUSTOMER_ID, BENEFICIARY_ID, new BigDecimal("-1000"), CURRENCY, EXPIRY_DATE)
            ).isInstanceOf(IllegalArgumentException.class)
             .hasMessageContaining("Amount");
        }

        @Test
        @DisplayName("Should fail when expiry date is in the past")
        void shouldFailWhenExpiryDateIsInPast() {
            assertThatThrownBy(() ->
                LetterOfCredit.create(TENANT_ID, CUSTOMER_ID, BENEFICIARY_ID, AMOUNT, CURRENCY, LocalDate.now().minusDays(1))
            ).isInstanceOf(IllegalArgumentException.class)
             .hasMessageContaining("expiry");
        }

        @Test
        @DisplayName("Should fail when currency is null")
        void shouldFailWhenCurrencyIsNull() {
            assertThatThrownBy(() ->
                LetterOfCredit.create(TENANT_ID, CUSTOMER_ID, BENEFICIARY_ID, AMOUNT, null, EXPIRY_DATE)
            ).isInstanceOf(IllegalArgumentException.class)
             .hasMessageContaining("Currency");
        }
    }

    @Nested
    @DisplayName("LC Lifecycle Tests")
    class LCLifecycleTests {

        private LetterOfCredit lc;

        @BeforeEach
        void setUp() {
            lc = LetterOfCredit.create(
                TENANT_ID,
                CUSTOMER_ID,
                BENEFICIARY_ID,
                AMOUNT,
                CURRENCY,
                EXPIRY_DATE
            );
        }

        @Test
        @DisplayName("Should submit LC for approval")
        void shouldSubmitLCForApproval() {
            // When
            lc.submit("user123");

            // Then
            assertThat(lc.getStatus()).isEqualTo(LCStatus.SUBMITTED);
        }

        @Test
        @DisplayName("Should approve LC")
        void shouldApproveLC() {
            // Given
            lc.submit("user123");

            // When
            lc.approve("approver123");

            // Then
            assertThat(lc.getStatus()).isEqualTo(LCStatus.APPROVED);
        }

        @Test
        @DisplayName("Should issue LC")
        void shouldIssueLC() {
            // Given
            lc.submit("user123");
            lc.approve("approver123");

            // When
            lc.issue("issuer123");

            // Then
            assertThat(lc.getStatus()).isEqualTo(LCStatus.ISSUED);
            assertThat(lc.getLcNumber()).isNotNull();
            assertThat(lc.getIssuedDate()).isNotNull();
        }

        @Test
        @DisplayName("Should reject LC")
        void shouldRejectLC() {
            // Given
            lc.submit("user123");

            // When
            lc.reject("approver123", "Insufficient credit");

            // Then
            assertThat(lc.getStatus()).isEqualTo(LCStatus.REJECTED);
        }

        @Test
        @DisplayName("Should fail to approve non-submitted LC")
        void shouldFailToApproveNonSubmittedLC() {
            assertThatThrownBy(() ->
                lc.approve("approver123")
            ).isInstanceOf(IllegalStateException.class)
             .hasMessageContaining("SUBMITTED");
        }

        @Test
        @DisplayName("Should fail to issue non-approved LC")
        void shouldFailToIssueNonApprovedLC() {
            assertThatThrownBy(() ->
                lc.issue("issuer123")
            ).isInstanceOf(IllegalStateException.class)
             .hasMessageContaining("APPROVED");
        }
    }

    @Nested
    @DisplayName("LC Amendment Tests")
    class LCAmendmentTests {

        private LetterOfCredit lc;

        @BeforeEach
        void setUp() {
            lc = LetterOfCredit.create(
                TENANT_ID,
                CUSTOMER_ID,
                BENEFICIARY_ID,
                AMOUNT,
                CURRENCY,
                EXPIRY_DATE
            );
            lc.submit("user");
            lc.approve("approver");
            lc.issue("issuer");
        }

        @Test
        @DisplayName("Should request amendment")
        void shouldRequestAmendment() {
            // Given
            BigDecimal newAmount = new BigDecimal("150000.00");
            String changes = "Increase amount to USD 150,000";

            // When
            lc.requestAmendment(newAmount, null, changes, "user123");

            // Then
            assertThat(lc.getStatus()).isEqualTo(LCStatus.AMENDMENT_REQUESTED);
        }

        @Test
        @DisplayName("Should approve amendment")
        void shouldApproveAmendment() {
            // Given
            BigDecimal newAmount = new BigDecimal("150000.00");
            lc.requestAmendment(newAmount, null, "Increase amount", "user");

            // When
            lc.approveAmendment("approver123");

            // Then
            assertThat(lc.getStatus()).isEqualTo(LCStatus.ISSUED);
            assertThat(lc.getAmount()).isEqualByComparingTo(newAmount);
        }

        @Test
        @DisplayName("Should reject amendment")
        void shouldRejectAmendment() {
            // Given
            BigDecimal newAmount = new BigDecimal("150000.00");
            lc.requestAmendment(newAmount, null, "Increase amount", "user");

            // When
            lc.rejectAmendment("approver123", "Exceeds credit limit");

            // Then
            assertThat(lc.getStatus()).isEqualTo(LCStatus.ISSUED);
            assertThat(lc.getAmount()).isEqualByComparingTo(AMOUNT); // Amount unchanged
        }

        @Test
        @DisplayName("Should extend expiry date via amendment")
        void shouldExtendExpiryDateViaAmendment() {
            // Given
            LocalDate newExpiryDate = EXPIRY_DATE.plusMonths(3);

            // When
            lc.requestAmendment(null, newExpiryDate, "Extend expiry", "user");
            lc.approveAmendment("approver");

            // Then
            assertThat(lc.getExpiryDate()).isEqualTo(newExpiryDate);
        }

        @Test
        @DisplayName("Should fail to amend non-issued LC")
        void shouldFailToAmendNonIssuedLC() {
            // Given
            LetterOfCredit draftLC = LetterOfCredit.create(
                TENANT_ID, CUSTOMER_ID, BENEFICIARY_ID, AMOUNT, CURRENCY, EXPIRY_DATE
            );

            // When/Then
            assertThatThrownBy(() ->
                draftLC.requestAmendment(new BigDecimal("200000"), null, "Changes", "user")
            ).isInstanceOf(IllegalStateException.class)
             .hasMessageContaining("ISSUED");
        }
    }

    @Nested
    @DisplayName("Document Presentation Tests")
    class DocumentPresentationTests {

        private LetterOfCredit lc;

        @BeforeEach
        void setUp() {
            lc = LetterOfCredit.create(
                TENANT_ID,
                CUSTOMER_ID,
                BENEFICIARY_ID,
                AMOUNT,
                CURRENCY,
                EXPIRY_DATE
            );
            lc.submit("user");
            lc.approve("approver");
            lc.issue("issuer");
        }

        @Test
        @DisplayName("Should present documents")
        void shouldPresentDocuments() {
            // When
            lc.presentDocuments(UUID.randomUUID(), "beneficiary");

            // Then
            assertThat(lc.getStatus()).isEqualTo(LCStatus.DOCUMENTS_PRESENTED);
            assertThat(lc.getPresentationDate()).isNotNull();
        }

        @Test
        @DisplayName("Should accept compliant documents")
        void shouldAcceptCompliantDocuments() {
            // Given
            lc.presentDocuments(UUID.randomUUID(), "beneficiary");

            // When
            lc.acceptDocuments("examiner");

            // Then
            assertThat(lc.getStatus()).isEqualTo(LCStatus.DOCUMENTS_ACCEPTED);
        }

        @Test
        @DisplayName("Should reject non-compliant documents")
        void shouldRejectNonCompliantDocuments() {
            // Given
            lc.presentDocuments(UUID.randomUUID(), "beneficiary");

            // When
            lc.rejectDocuments("examiner", "Discrepancies found");

            // Then
            assertThat(lc.getStatus()).isEqualTo(LCStatus.DOCUMENTS_REJECTED);
        }
    }

    @Nested
    @DisplayName("Payment Tests")
    class PaymentTests {

        private LetterOfCredit lc;

        @BeforeEach
        void setUp() {
            lc = LetterOfCredit.create(
                TENANT_ID,
                CUSTOMER_ID,
                BENEFICIARY_ID,
                AMOUNT,
                CURRENCY,
                EXPIRY_DATE
            );
            lc.submit("user");
            lc.approve("approver");
            lc.issue("issuer");
            lc.presentDocuments(UUID.randomUUID(), "beneficiary");
            lc.acceptDocuments("examiner");
        }

        @Test
        @DisplayName("Should authorize payment")
        void shouldAuthorizePayment() {
            // When
            lc.authorizePayment("authorizer");

            // Then
            assertThat(lc.getStatus()).isEqualTo(LCStatus.PAYMENT_AUTHORIZED);
        }

        @Test
        @DisplayName("Should complete payment")
        void shouldCompletePayment() {
            // Given
            lc.authorizePayment("authorizer");

            // When
            lc.completePayment(UUID.randomUUID(), "payment_processor");

            // Then
            assertThat(lc.getStatus()).isEqualTo(LCStatus.COMPLETED);
            assertThat(lc.getPaymentDate()).isNotNull();
        }

        @Test
        @DisplayName("Should fail to complete payment without authorization")
        void shouldFailToCompletePaymentWithoutAuthorization() {
            assertThatThrownBy(() ->
                lc.completePayment(UUID.randomUUID(), "processor")
            ).isInstanceOf(IllegalStateException.class)
             .hasMessageContaining("PAYMENT_AUTHORIZED");
        }
    }

    @Nested
    @DisplayName("Business Rule Tests")
    class BusinessRuleTests {

        @Test
        @DisplayName("Should enforce minimum LC amount")
        void shouldEnforceMinimumLCAmount() {
            // Assuming minimum LC amount is USD 1,000
            assertThatThrownBy(() ->
                LetterOfCredit.create(TENANT_ID, CUSTOMER_ID, BENEFICIARY_ID,
                    new BigDecimal("500"), CURRENCY, EXPIRY_DATE)
            ).isInstanceOf(IllegalArgumentException.class)
             .hasMessageContaining("minimum");
        }

        @Test
        @DisplayName("Should enforce maximum LC validity period")
        void shouldEnforceMaximumValidityPeriod() {
            // Assuming maximum validity is 12 months
            LocalDate tooFarFuture = LocalDate.now().plusMonths(13);

            assertThatThrownBy(() ->
                LetterOfCredit.create(TENANT_ID, CUSTOMER_ID, BENEFICIARY_ID,
                    AMOUNT, CURRENCY, tooFarFuture)
            ).isInstanceOf(IllegalArgumentException.class)
             .hasMessageContaining("validity period");
        }

        @Test
        @DisplayName("Should not allow payment before document acceptance")
        void shouldNotAllowPaymentBeforeDocumentAcceptance() {
            // Given
            LetterOfCredit lc = LetterOfCredit.create(
                TENANT_ID, CUSTOMER_ID, BENEFICIARY_ID, AMOUNT, CURRENCY, EXPIRY_DATE
            );
            lc.submit("user");
            lc.approve("approver");
            lc.issue("issuer");

            // When/Then
            assertThatThrownBy(() ->
                lc.authorizePayment("authorizer")
            ).isInstanceOf(IllegalStateException.class)
             .hasMessageContaining("DOCUMENTS_ACCEPTED");
        }

        @Test
        @DisplayName("Should detect expired LC")
        void shouldDetectExpiredLC() {
            // Given
            LocalDate nearExpiry = LocalDate.now().plusDays(1);
            LetterOfCredit lc = LetterOfCredit.create(
                TENANT_ID, CUSTOMER_ID, BENEFICIARY_ID, AMOUNT, CURRENCY, nearExpiry
            );

            // When
            boolean isExpiringSoon = lc.isExpiringSoon(7); // Within 7 days

            // Then
            assertThat(isExpiringSoon).isTrue();
        }
    }

    @Nested
    @DisplayName("LC Number Generation Tests")
    class LCNumberGenerationTests {

        @Test
        @DisplayName("Should generate unique LC numbers")
        void shouldGenerateUniqueLCNumbers() {
            // Given
            LetterOfCredit lc1 = LetterOfCredit.create(
                TENANT_ID, CUSTOMER_ID, BENEFICIARY_ID, AMOUNT, CURRENCY, EXPIRY_DATE
            );
            LetterOfCredit lc2 = LetterOfCredit.create(
                TENANT_ID, CUSTOMER_ID, BENEFICIARY_ID, AMOUNT, CURRENCY, EXPIRY_DATE
            );

            lc1.submit("user");
            lc1.approve("approver");
            lc1.issue("issuer");

            lc2.submit("user");
            lc2.approve("approver");
            lc2.issue("issuer");

            // Then
            assertThat(lc1.getLcNumber()).isNotNull();
            assertThat(lc2.getLcNumber()).isNotNull();
            assertThat(lc1.getLcNumber()).isNotEqualTo(lc2.getLcNumber());
        }

        @Test
        @DisplayName("Should follow LC number format")
        void shouldFollowLCNumberFormat() {
            // Given
            LetterOfCredit lc = LetterOfCredit.create(
                TENANT_ID, CUSTOMER_ID, BENEFICIARY_ID, AMOUNT, CURRENCY, EXPIRY_DATE
            );
            lc.submit("user");
            lc.approve("approver");
            lc.issue("issuer");

            // Then
            assertThat(lc.getLcNumber()).matches("LC-\\d{4}-\\d{8}");
        }
    }
}


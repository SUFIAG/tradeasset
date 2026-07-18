package com.tradesphere.ai.lc.domain.model;

import com.tradesphere.ai.customer.domain.model.CustomerId;
import com.tradesphere.ai.shared.domain.valueobject.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

/**
 * Comprehensive unit tests for Letter of Credit domain model
 * Tests LC lifecycle, amendments, business rules
 */
@DisplayName("Letter of Credit Domain Model Tests")
class LetterOfCreditTest {

    private static final UUID TENANT_ID = UUID.randomUUID();
    private static final CustomerId APPLICANT_ID = CustomerId.generate();
    private static final CustomerId BENEFICIARY_ID = CustomerId.generate();
    private static final BigDecimal AMOUNT = new BigDecimal("100000.00");
    private static final String CURRENCY = "USD";
    private static final LocalDate EXPIRY_DATE = LocalDate.now().plusMonths(6);
    private static final UUID USER_ID = UUID.randomUUID();

    private LetterOfCredit createTestLC() {
        return LetterOfCredit.createNew(
            TENANT_ID,
            APPLICANT_ID,
            "Test Applicant",
            BENEFICIARY_ID,
            "Test Beneficiary",
            Money.of(AMOUNT, CURRENCY),
            LCType.SIGHT,
            EXPIRY_DATE,
            USER_ID
        );
    }

    @Nested
    @DisplayName("LC Creation Tests")
    class LCCreationTests {

        @Test
        @DisplayName("Should create LC in DRAFT status")
        void shouldCreateLCInDraftStatus() {
            // When
            LetterOfCredit lc = createTestLC();

            // Then
            assertThat(lc).isNotNull();
            assertThat(lc.getId()).isNotNull();
            assertThat(lc.getTenantId()).isEqualTo(TENANT_ID);
            assertThat(lc.getApplicantId()).isEqualTo(APPLICANT_ID);
            assertThat(lc.getBeneficiaryId()).isEqualTo(BENEFICIARY_ID);
            assertThat(lc.getLcAmount().getAmount()).isEqualByComparingTo(AMOUNT);
            assertThat(lc.getCurrency()).isEqualTo(CURRENCY);
            assertThat(lc.getExpiryDate()).isEqualTo(EXPIRY_DATE);
            assertThat(lc.getStatus()).isEqualTo(LCStatus.DRAFT);
        }

        @Test
        @DisplayName("Should fail when amount is null")
        void shouldFailWhenAmountIsNull() {
            assertThatThrownBy(() ->
                LetterOfCredit.createNew(TENANT_ID, APPLICANT_ID, "Test", BENEFICIARY_ID, "Test",
                    null, LCType.SIGHT, EXPIRY_DATE, USER_ID)
            ).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Should fail when amount is zero")
        void shouldFailWhenAmountIsZero() {
            assertThatThrownBy(() ->
                LetterOfCredit.createNew(TENANT_ID, APPLICANT_ID, "Test", BENEFICIARY_ID, "Test",
                    Money.of(BigDecimal.ZERO, CURRENCY), LCType.SIGHT, EXPIRY_DATE, USER_ID)
            ).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Should fail when amount is negative")
        void shouldFailWhenAmountIsNegative() {
            assertThatThrownBy(() ->
                LetterOfCredit.createNew(TENANT_ID, APPLICANT_ID, "Test", BENEFICIARY_ID, "Test",
                    Money.of(new BigDecimal("-1000"), CURRENCY), LCType.SIGHT, EXPIRY_DATE, USER_ID)
            ).isInstanceOf(IllegalArgumentException.class)
             .hasMessageContaining("Amount");
        }

        @Test
        @DisplayName("Should fail when expiry date is in the past")
        void shouldFailWhenExpiryDateIsInPast() {
            assertThatThrownBy(() ->
                LetterOfCredit.createNew(TENANT_ID, APPLICANT_ID, "Test", BENEFICIARY_ID, "Test",
                    Money.of(AMOUNT, CURRENCY), LCType.SIGHT, LocalDate.now().minusDays(1), USER_ID)
            ).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("LC Lifecycle Tests")
    class LCLifecycleTests {

        private LetterOfCredit lc;

        @BeforeEach
        void setUp() {
            lc = createTestLC();
        }

        @Test
        @DisplayName("Should submit LC for approval")
        void shouldSubmitLCForApproval() {
            // When
            lc.submitForApproval(USER_ID);

            // Then
            assertThat(lc.getStatus()).isEqualTo(LCStatus.SUBMITTED);
        }

        @Test
        @DisplayName("Should approve LC")
        void shouldApproveLC() {
            // Given
            lc.submitForApproval(USER_ID);

            // When
            lc.approve(USER_ID, true);

            // Then
            assertThat(lc.getStatus()).isEqualTo(LCStatus.APPROVED);
        }

        @Test
        @DisplayName("Should issue LC")
        void shouldIssueLC() {
            // Given
            lc.submitForApproval(USER_ID);
            lc.approve(USER_ID, true);

            // When
            lc.issue(LCNumber.generate(Year.now().getValue(), 1L), USER_ID);

            // Then
            assertThat(lc.getStatus()).isEqualTo(LCStatus.ISSUED);
            assertThat(lc.getLcNumber()).isNotNull();
        }

        @Test
        @DisplayName("Should reject LC")
        void shouldRejectLC() {
            // Given
            lc.submitForApproval(USER_ID);

            // When
            lc.reject(USER_ID, "Insufficient credit");

            // Then
            assertThat(lc.getStatus()).isEqualTo(LCStatus.REJECTED);
        }

        @Test
        @DisplayName("Should fail to approve non-submitted LC")
        void shouldFailToApproveNonSubmittedLC() {
            assertThatThrownBy(() ->
                lc.approve(USER_ID, true)
            ).isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("Should fail to issue non-approved LC")
        void shouldFailToIssueNonApprovedLC() {
            assertThatThrownBy(() ->
                lc.issue(LCNumber.generate(Year.now().getValue(), 1L), USER_ID)
            ).isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("LC Amendment Tests")
    class LCAmendmentTests {

        private LetterOfCredit lc;

        @BeforeEach
        void setUp() {
            lc = createTestLC();
            lc.submitForApproval(USER_ID);
            lc.approve(USER_ID, true);
            lc.issue(LCNumber.generate(Year.now().getValue(), 1L), USER_ID);
        }

        @Test
        @DisplayName("Should request amendment")
        void shouldRequestAmendment() {
            // Given
            String changes = "Increase amount to USD 150,000";

            // When
            lc.requestAmendment(changes, USER_ID);

            // Then
            assertThat(lc.getStatus()).isEqualTo(LCStatus.AMENDMENT_REQUESTED);
        }

        @Test
        @DisplayName("Should approve amendment")
        void shouldApproveAmendment() {
            // Given
            Money newAmount = Money.of(new BigDecimal("150000.00"), CURRENCY);
            String changes = "Increase amount";
            lc.requestAmendment(changes, USER_ID);

            // When
            lc.approveAmendment(newAmount, null, null, USER_ID);

            // Then
            assertThat(lc.getStatus()).isEqualTo(LCStatus.AMENDED);
            assertThat(lc.getLcAmount().getAmount()).isEqualByComparingTo(newAmount.getAmount());
        }

        @Test
        @DisplayName("Should reject amendment")
        void shouldRejectAmendment() {
            // Given
            String changes = "Increase amount";
            lc.requestAmendment(changes, USER_ID);

            // When
            lc.rejectAmendment("Exceeds credit limit", USER_ID);

            // Then
            assertThat(lc.getStatus()).isEqualTo(LCStatus.ISSUED);
            assertThat(lc.getLcAmount().getAmount()).isEqualByComparingTo(AMOUNT);
        }

        @Test
        @DisplayName("Should extend expiry date via amendment")
        void shouldExtendExpiryDateViaAmendment() {
            // Given
            LocalDate newExpiryDate = EXPIRY_DATE.plusMonths(3);
            String changes = "Extend expiry";

            // When
            lc.requestAmendment(changes, USER_ID);
            lc.approveAmendment(null, newExpiryDate, null, USER_ID);

            // Then
            assertThat(lc.getExpiryDate()).isEqualTo(newExpiryDate);
        }

        @Test
        @DisplayName("Should fail to amend non-issued LC")
        void shouldFailToAmendNonIssuedLC() {
            // Given
            LetterOfCredit draftLC = createTestLC();

            // When/Then
            assertThatThrownBy(() ->
                draftLC.requestAmendment("Changes", USER_ID)
            ).isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("Document Presentation Tests")
    class DocumentPresentationTests {

        private LetterOfCredit lc;

        @BeforeEach
        void setUp() {
            lc = createTestLC();
            lc.submitForApproval(USER_ID);
            lc.approve(USER_ID, true);
            lc.issue(LCNumber.generate(Year.now().getValue(), 1L), USER_ID);
        }

        @Test
        @DisplayName("Should receive documents")
        void shouldReceiveDocuments() {
            // When
            lc.receiveDocuments(USER_ID);

            // Then
            assertThat(lc.getStatus()).isEqualTo(LCStatus.DOCUMENTS_RECEIVED);
        }

        @Test
        @DisplayName("Should accept compliant documents")
        void shouldAcceptCompliantDocuments() {
            // Given
            lc.receiveDocuments(USER_ID);

            // When
            lc.acceptDocuments(USER_ID);

            // Then
            assertThat(lc.getStatus()).isEqualTo(LCStatus.DOCUMENTS_ACCEPTED);
        }

        @Test
        @DisplayName("Should reject non-compliant documents")
        void shouldRejectNonCompliantDocuments() {
            // Given
            lc.receiveDocuments(USER_ID);

            // When
            lc.rejectDocuments("Discrepancies found", USER_ID);

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
            lc = createTestLC();
            lc.submitForApproval(USER_ID);
            lc.approve(USER_ID, true);
            lc.issue(LCNumber.generate(Year.now().getValue(), 1L), USER_ID);
            lc.receiveDocuments(USER_ID);
            lc.acceptDocuments(USER_ID);
        }

        @Test
        @DisplayName("Should authorize payment")
        void shouldAuthorizePayment() {
            // When
            lc.authorizePayment(Money.of(AMOUNT, CURRENCY), UUID.randomUUID());

            // Then
            assertThat(lc.getStatus()).isEqualTo(LCStatus.PAYMENT_AUTHORIZED);
        }

        @Test
        @DisplayName("Should complete payment")
        void shouldCompletePayment() {
            // Given
            lc.authorizePayment(Money.of(AMOUNT, CURRENCY), UUID.randomUUID());

            // When
            lc.completePayment(UUID.randomUUID());

            // Then
            assertThat(lc.getStatus()).isEqualTo(LCStatus.PAID);
        }

        @Test
        @DisplayName("Should fail to complete payment without authorization")
        void shouldFailToCompletePaymentWithoutAuthorization() {
            assertThatThrownBy(() ->
                lc.completePayment(UUID.randomUUID())
            ).isInstanceOf(IllegalStateException.class)
             .hasMessageContaining("PAYMENT_AUTHORIZED");
        }
    }

    @Nested
    @DisplayName("Business Rule Tests")
    class BusinessRuleTests {

        @Test
        @DisplayName("Should not allow payment before document acceptance")
        void shouldNotAllowPaymentBeforeDocumentAcceptance() {
            // Given
            LetterOfCredit lc = createTestLC();
            lc.submitForApproval(USER_ID);
            lc.approve(USER_ID, true);
            lc.issue(LCNumber.generate(Year.now().getValue(), 1L), USER_ID);

            // When/Then
            assertThatThrownBy(() ->
                lc.authorizePayment(Money.of(AMOUNT, CURRENCY), USER_ID)
            ).isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("Should detect expired LC")
        void shouldDetectExpiredLC() {
            // Given
            LocalDate nearExpiry = LocalDate.now().minusDays(1);
            LetterOfCredit lc = LetterOfCredit.createNew(TENANT_ID, APPLICANT_ID, "Test",
                BENEFICIARY_ID, "Test", Money.of(AMOUNT, CURRENCY), LCType.SIGHT, nearExpiry, USER_ID);

            // When
            boolean isExpired = lc.isExpired();

            // Then
            assertThat(isExpired).isTrue();
        }
    }

    @Nested
    @DisplayName("LC Number Generation Tests")
    class LCNumberGenerationTests {

        @Test
        @DisplayName("Should generate unique LC numbers")
        void shouldGenerateUniqueLCNumbers() {
            // Given
            LetterOfCredit lc1 = createTestLC();
            LetterOfCredit lc2 = createTestLC();

            lc1.submitForApproval(USER_ID);
            lc1.approve(USER_ID, true);
            LCNumber number1 = LCNumber.generate(Year.now().getValue(), 1L);
            lc1.issue(number1, USER_ID);

            lc2.submitForApproval(USER_ID);
            lc2.approve(USER_ID, true);
            LCNumber number2 = LCNumber.generate(Year.now().getValue(), 2L);
            lc2.issue(number2, USER_ID);

            // Then
            assertThat(lc1.getLcNumber()).isNotNull();
            assertThat(lc2.getLcNumber()).isNotNull();
            assertThat(lc1.getLcNumber()).isNotEqualTo(lc2.getLcNumber());
        }

        @Test
        @DisplayName("Should follow LC number format")
        void shouldFollowLCNumberFormat() {
            // Given
            LetterOfCredit lc = createTestLC();
            lc.submitForApproval(USER_ID);
            lc.approve(USER_ID, true);
            lc.issue(LCNumber.generate(Year.now().getValue(), 1L), USER_ID);

            // Then
            assertThat(lc.getLcNumber().value()).matches("LC-\\d{4}-\\d{6}");
        }
    }
}


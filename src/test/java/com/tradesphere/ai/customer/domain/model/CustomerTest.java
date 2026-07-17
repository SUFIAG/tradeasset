package com.tradesphere.ai.customer.domain.model;

import com.tradesphere.ai.shared.domain.valueobject.Address;
import com.tradesphere.ai.shared.domain.valueobject.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for Customer aggregate
 * Tests all business rules and state transitions
 */
@DisplayName("Customer Aggregate Tests")
class CustomerTest {

    private static final UUID TENANT_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String CUSTOMER_NAME = "Acme Corporation";
    private static final String EMAIL = "contact@acme.com";
    private static final CustomerType TYPE = CustomerType.IMPORTER;

    @Nested
    @DisplayName("Customer Creation Tests")
    class CustomerCreationTests {

        @Test
        @DisplayName("Should create customer with valid data")
        void shouldCreateCustomerWithValidData() {
            // When
            Customer customer = Customer.create(
                    TENANT_ID,
                    CUSTOMER_NAME,
                    EMAIL,
                    TYPE,
                    USER_ID
            );

            // Then
            assertNotNull(customer);
            assertNotNull(customer.getId());
            assertEquals(TENANT_ID, customer.getTenantId());
            assertEquals(CUSTOMER_NAME, customer.getName());
            assertEquals(EMAIL, customer.getEmail());
            assertEquals(TYPE, customer.getType());
            assertEquals(CustomerStatus.DRAFT, customer.getStatus());
            assertEquals(KYCStatus.PENDING, customer.getKycStatus());
            assertEquals(ComplianceStatus.PENDING, customer.getComplianceStatus());
            assertEquals(RiskRating.NOT_ASSESSED, customer.getRiskRating());
            assertNotNull(customer.getCustomerNumber());
            assertTrue(customer.getCustomerNumber().startsWith("CUST-"));

            // Verify domain event
            assertEquals(1, customer.getDomainEvents().size());
            assertTrue(customer.getDomainEvents().get(0) instanceof CustomerCreated);
        }

        @Test
        @DisplayName("Should fail when tenant ID is null")
        void shouldFailWhenTenantIdIsNull() {
            // When & Then
            assertThrows(CustomerException.class, () ->
                    Customer.create(null, CUSTOMER_NAME, EMAIL, TYPE, USER_ID)
            );
        }

        @Test
        @DisplayName("Should fail when name is blank")
        void shouldFailWhenNameIsBlank() {
            // When & Then
            assertThrows(CustomerException.class, () ->
                    Customer.create(TENANT_ID, "", EMAIL, TYPE, USER_ID)
            );
        }

        @Test
        @DisplayName("Should fail when email is invalid")
        void shouldFailWhenEmailIsInvalid() {
            // When & Then
            assertThrows(CustomerException.class, () ->
                    Customer.create(TENANT_ID, CUSTOMER_NAME, "invalid-email", TYPE, USER_ID)
            );
        }

        @Test
        @DisplayName("Should fail when type is null")
        void shouldFailWhenTypeIsNull() {
            // When & Then
            assertThrows(CustomerException.class, () ->
                    Customer.create(TENANT_ID, CUSTOMER_NAME, EMAIL, null, USER_ID)
            );
        }
    }

    @Nested
    @DisplayName("Customer Approval Workflow Tests")
    class CustomerApprovalWorkflowTests {

        @Test
        @DisplayName("Should submit customer for approval")
        void shouldSubmitCustomerForApproval() {
            // Given
            Customer customer = createDraftCustomer();

            // When
            customer.submitForApproval();

            // Then
            assertEquals(CustomerStatus.PENDING_APPROVAL, customer.getStatus());
            assertEquals(2, customer.getDomainEvents().size());
            assertTrue(customer.getDomainEvents().get(1) instanceof CustomerSubmittedForApproval);
        }

        @Test
        @DisplayName("Should approve customer with KYC and compliance")
        void shouldApproveCustomerWithKYCAndCompliance() {
            // Given
            Customer customer = createPendingApprovalCustomer();
            customer.completeKYC(LocalDate.now().plusYears(1), USER_ID);
            customer.updateComplianceStatus(ComplianceStatus.CLEARED, USER_ID);

            // When
            customer.approve(USER_ID);

            // Then
            assertEquals(CustomerStatus.APPROVED, customer.getStatus());
            assertNotNull(customer.getApprovedAt());
            assertEquals(USER_ID, customer.getApprovedBy());
        }

        @Test
        @DisplayName("Should fail approval without KYC")
        void shouldFailApprovalWithoutKYC() {
            // Given
            Customer customer = createPendingApprovalCustomer();

            // When & Then
            CustomerException exception = assertThrows(CustomerException.class, () ->
                    customer.approve(USER_ID)
            );
            assertTrue(exception.getMessage().contains("KYC must be completed"));
        }

        @Test
        @DisplayName("Should fail approval without compliance clearance")
        void shouldFailApprovalWithoutComplianceClearance() {
            // Given
            Customer customer = createPendingApprovalCustomer();
            customer.completeKYC(LocalDate.now().plusYears(1), USER_ID);

            // When & Then
            CustomerException exception = assertThrows(CustomerException.class, () ->
                    customer.approve(USER_ID)
            );
            assertTrue(exception.getMessage().contains("Compliance must be cleared"));
        }

        @Test
        @DisplayName("Should reject customer with reason")
        void shouldRejectCustomerWithReason() {
            // Given
            Customer customer = createPendingApprovalCustomer();
            String reason = "Incomplete documentation";

            // When
            customer.reject(reason, USER_ID);

            // Then
            assertEquals(CustomerStatus.REJECTED, customer.getStatus());
            assertEquals(reason, customer.getRejectionReason());
            assertNotNull(customer.getRejectedAt());
            assertEquals(USER_ID, customer.getRejectedBy());
        }
    }

    @Nested
    @DisplayName("Customer Lifecycle Tests")
    class CustomerLifecycleTests {

        @Test
        @DisplayName("Should activate approved customer")
        void shouldActivateApprovedCustomer() {
            // Given
            Customer customer = createApprovedCustomer();

            // When
            customer.activate();

            // Then
            assertEquals(CustomerStatus.ACTIVE, customer.getStatus());
        }

        @Test
        @DisplayName("Should suspend active customer")
        void shouldSuspendActiveCustomer() {
            // Given
            Customer customer = createActiveCustomer();
            String reason = "Suspicious activity";

            // When
            customer.suspend(reason, USER_ID);

            // Then
            assertEquals(CustomerStatus.SUSPENDED, customer.getStatus());
            assertEquals(reason, customer.getSuspensionReason());
        }

        @Test
        @DisplayName("Should reactivate suspended customer")
        void shouldReactivateSuspendedCustomer() {
            // Given
            Customer customer = createActiveCustomer();
            customer.suspend("Test", USER_ID);

            // When
            customer.reactivate(USER_ID);

            // Then
            assertEquals(CustomerStatus.ACTIVE, customer.getStatus());
            assertNull(customer.getSuspensionReason());
        }

        @Test
        @DisplayName("Only ACTIVE customers can transact")
        void onlyActiveCustomersCanTransact() {
            // Given
            Customer draftCustomer = createDraftCustomer();
            Customer activeCustomer = createActiveCustomer();

            // Then
            assertFalse(draftCustomer.getStatus().canTransact());
            assertTrue(activeCustomer.getStatus().canTransact());
        }
    }

    @Nested
    @DisplayName("KYC Management Tests")
    class KYCManagementTests {

        @Test
        @DisplayName("Should complete KYC with expiry date")
        void shouldCompleteKYCWithExpiryDate() {
            // Given
            Customer customer = createDraftCustomer();
            LocalDate expiryDate = LocalDate.now().plusYears(1);

            // When
            customer.completeKYC(expiryDate, USER_ID);

            // Then
            assertEquals(KYCStatus.COMPLETED, customer.getKycStatus());
            assertEquals(expiryDate, customer.getKycExpiryDate());
            assertNotNull(customer.getKycCompletedAt());
        }

        @Test
        @DisplayName("Should fail KYC completion with past expiry date")
        void shouldFailKYCCompletionWithPastExpiryDate() {
            // Given
            Customer customer = createDraftCustomer();
            LocalDate pastDate = LocalDate.now().minusDays(1);

            // When & Then
            assertThrows(CustomerException.class, () ->
                    customer.completeKYC(pastDate, USER_ID)
            );
        }

        @Test
        @DisplayName("Should update next KYC review date")
        void shouldUpdateNextKYCReviewDate() {
            // Given
            Customer customer = createActiveCustomer();
            LocalDate reviewDate = LocalDate.now().plusMonths(6);

            // When
            customer.updateNextKYCReviewDate(reviewDate);

            // Then
            assertEquals(reviewDate, customer.getNextKycReviewDate());
        }
    }

    @Nested
    @DisplayName("Credit Management Tests")
    class CreditManagementTests {

        @Test
        @DisplayName("Should set credit limit with risk assessment")
        void shouldSetCreditLimitWithRiskAssessment() {
            // Given
            Customer customer = createActiveCustomer();
            customer.updateRiskRating(RiskRating.LOW, 25, USER_ID);
            Money creditLimit = Money.of(BigDecimal.valueOf(1000000), Currency.getInstance("USD"));

            // When
            customer.setCreditLimit(creditLimit, USER_ID);

            // Then
            assertEquals(creditLimit, customer.getCreditLimit());
            assertEquals(creditLimit, customer.getAvailableCredit());
        }

        @Test
        @DisplayName("Should fail setting credit limit without risk assessment")
        void shouldFailSettingCreditLimitWithoutRiskAssessment() {
            // Given
            Customer customer = createActiveCustomer();
            Money creditLimit = Money.of(BigDecimal.valueOf(1000000), Currency.getInstance("USD"));

            // When & Then
            CustomerException exception = assertThrows(CustomerException.class, () ->
                    customer.setCreditLimit(creditLimit, USER_ID)
            );
            assertTrue(exception.getMessage().contains("Risk must be assessed"));
        }

        @Test
        @DisplayName("Should reserve credit successfully")
        void shouldReserveCreditSuccessfully() {
            // Given
            Customer customer = createCustomerWithCreditLimit();
            Money reserveAmount = Money.of(BigDecimal.valueOf(500000), Currency.getInstance("USD"));

            // When
            customer.reserveCredit(reserveAmount);

            // Then
            Money expectedAvailable = Money.of(BigDecimal.valueOf(500000), Currency.getInstance("USD"));
            assertEquals(expectedAvailable, customer.getAvailableCredit());
        }

        @Test
        @DisplayName("Should fail reserving more than available credit")
        void shouldFailReservingMoreThanAvailableCredit() {
            // Given
            Customer customer = createCustomerWithCreditLimit();
            Money excessAmount = Money.of(BigDecimal.valueOf(2000000), Currency.getInstance("USD"));

            // When & Then
            assertThrows(CustomerException.class, () ->
                    customer.reserveCredit(excessAmount)
            );
        }

        @Test
        @DisplayName("Should release reserved credit")
        void shouldReleaseReservedCredit() {
            // Given
            Customer customer = createCustomerWithCreditLimit();
            Money reserveAmount = Money.of(BigDecimal.valueOf(500000), Currency.getInstance("USD"));
            customer.reserveCredit(reserveAmount);

            // When
            customer.releaseCredit(reserveAmount);

            // Then
            assertEquals(customer.getCreditLimit(), customer.getAvailableCredit());
        }
    }

    @Nested
    @DisplayName("Risk Management Tests")
    class RiskManagementTests {

        @Test
        @DisplayName("Should update risk rating with score")
        void shouldUpdateRiskRatingWithScore() {
            // Given
            Customer customer = createActiveCustomer();
            int score = 75;

            // When
            customer.updateRiskRating(RiskRating.HIGH, score, USER_ID);

            // Then
            assertEquals(RiskRating.HIGH, customer.getRiskRating());
            assertEquals(score, customer.getRiskScore());
            assertNotNull(customer.getLastRiskAssessmentDate());
        }

        @Test
        @DisplayName("Should auto-elevate risk for PEP customers")
        void shouldAutoElevateRiskForPEPCustomers() {
            // Given
            Customer customer = createActiveCustomer();
            customer.updateRiskRating(RiskRating.LOW, 20, USER_ID);

            // When
            customer.markAsPEP(true, USER_ID);

            // Then
            assertTrue(customer.isPoliticallyExposed());
            assertEquals(RiskRating.HIGH, customer.getRiskRating());
        }

        @Test
        @DisplayName("Should flag sanctions hit")
        void shouldFlagSanctionsHit() {
            // Given
            Customer customer = createActiveCustomer();

            // When
            customer.markSanctionsHit(true, "OFAC match", USER_ID);

            // Then
            assertTrue(customer.isSanctionsHit());
            assertEquals("OFAC match", customer.getSanctionsDetails());
        }
    }

    @Nested
    @DisplayName("Compliance Management Tests")
    class ComplianceManagementTests {

        @Test
        @DisplayName("Should update compliance status")
        void shouldUpdateComplianceStatus() {
            // Given
            Customer customer = createDraftCustomer();

            // When
            customer.updateComplianceStatus(ComplianceStatus.CLEARED, USER_ID);

            // Then
            assertEquals(ComplianceStatus.CLEARED, customer.getComplianceStatus());
        }

        @Test
        @DisplayName("Should mark as high risk country")
        void shouldMarkAsHighRiskCountry() {
            // Given
            Customer customer = createActiveCustomer();

            // When
            customer.markHighRiskCountry(true);

            // Then
            assertTrue(customer.isHighRiskCountry());
        }
    }


    // Helper methods to create customers in different states

    private Customer createDraftCustomer() {
        return Customer.create(TENANT_ID, CUSTOMER_NAME, LEGAL_NAME, TYPE, EMAIL, USER_ID);
    }

    private Customer createPendingApprovalCustomer() {
        Customer customer = createDraftCustomer();
        customer.submitForApproval(USER_ID);
        return customer;
    }

    private Customer createApprovedCustomer() {
        Customer customer = createPendingApprovalCustomer();
        customer.completeKYC(LocalDate.now().plusYears(1), USER_ID);
        customer.updateComplianceStatus(ComplianceStatus.CLEARED, USER_ID);
        customer.approve(USER_ID);
        return customer;
    }

    private Customer createActiveCustomer() {
        Customer customer = createApprovedCustomer();
        customer.activate(USER_ID);
        return customer;
    }

    private Customer createCustomerWithCreditLimit() {
        Customer customer = createActiveCustomer();
        customer.updateRiskRating(RiskRating.LOW, BigDecimal.valueOf(25), "Low risk", USER_ID);
        Money creditLimit = Money.of(BigDecimal.valueOf(1000000), Currency.getInstance("USD"));
        customer.setCreditLimit(creditLimit, USER_ID);
        return customer;
    }
}




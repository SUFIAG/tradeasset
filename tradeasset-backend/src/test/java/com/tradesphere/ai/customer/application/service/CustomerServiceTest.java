package com.tradesphere.ai.customer.application.service;

import com.tradesphere.ai.customer.application.port.in.*;
import com.tradesphere.ai.customer.application.port.out.*;
import com.tradesphere.ai.customer.domain.model.*;
import com.tradesphere.ai.shared.domain.valueobject.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CustomerService
 * Tests use case implementations with mocked dependencies
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Customer Service Tests")
class CustomerServiceTest {

    @Mock
    private LoadCustomerPort loadCustomerPort;

    @Mock
    private SaveCustomerPort saveCustomerPort;

    @Mock
    private CustomerExistsPort customerExistsPort;

    @Mock
    private PublishCustomerEventPort eventPublisher;

    @Mock
    private ListCustomersPort listCustomersPort;

    private CustomerService customerService;

    private static final UUID TENANT_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String CUSTOMER_NAME = "Test Customer";
    private static final String LEGAL_NAME = "Test Customer Legal Ltd";
    private static final String EMAIL = "test@customer.com";

    @BeforeEach
    void setUp() {
        customerService = new CustomerService(
                loadCustomerPort,
                saveCustomerPort,
                customerExistsPort,
                eventPublisher,
                listCustomersPort
        );
    }

    @Test
    @DisplayName("Should create customer successfully")
    void shouldCreateCustomerSuccessfully() {
        // Given
        CreateCustomerCommand command = CreateCustomerCommand.builder()
                .tenantId(TENANT_ID)
                .name(CUSTOMER_NAME)
                .legalName(LEGAL_NAME)
                .type(CustomerType.IMPORTER)
                .email(EMAIL)
                .createdBy(USER_ID)
                .build();

        when(customerExistsPort.existsByEmail(anyString())).thenReturn(false);
        when(saveCustomerPort.saveCustomer(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Customer customer = customerService.createCustomer(command);

        // Then
        assertNotNull(customer);
        assertNotNull(customer.getId());
        assertEquals(CUSTOMER_NAME, customer.getName());
        verify(saveCustomerPort, times(1)).saveCustomer(any(Customer.class));
        verify(eventPublisher, atLeastOnce()).publish(any());
    }

    @Test
    @DisplayName("Should approve customer when KYC and compliance are complete")
    void shouldApproveCustomerWhenRequirementsMet() {
        // Given
        Customer customer = createTestCustomer();
        customer.submitForApproval(USER_ID);
        customer.completeKYC(LocalDate.now().plusYears(1), USER_ID);
        customer.updateComplianceStatus(ComplianceStatus.CLEARED, USER_ID);

        ApproveCustomerCommand command = ApproveCustomerCommand.builder()
                .customerId(customer.getId().toString())
                .approvedBy(USER_ID)
                .build();

        when(loadCustomerPort.loadCustomerById(any(CustomerId.class)))
                .thenReturn(Optional.of(customer));
        when(saveCustomerPort.saveCustomer(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        customerService.approveCustomer(command);

        // Then
        assertEquals(CustomerStatus.APPROVED, customer.getStatus());
        verify(saveCustomerPort, times(1)).saveCustomer(customer);
        verify(eventPublisher, atLeastOnce()).publish(any());
    }

    @Test
    @DisplayName("Should set credit limit for customer with risk assessment")
    void shouldSetCreditLimitWithRiskAssessment() {
        // Given
        Customer customer = createActiveCustomer();
        customer.updateRiskRating(RiskRating.LOW, BigDecimal.valueOf(30), "Low risk assessment", USER_ID);

        Money creditLimit = Money.of(BigDecimal.valueOf(1000000), Currency.getInstance("USD"));
        SetCreditLimitCommand command = SetCreditLimitCommand.builder()
                .customerId(customer.getId().toString())
                .creditLimit(creditLimit)
                .approvedBy(USER_ID)
                .build();

        when(loadCustomerPort.loadCustomerById(any(CustomerId.class)))
                .thenReturn(Optional.of(customer));
        when(saveCustomerPort.saveCustomer(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        customerService.setCreditLimit(command);

        // Then
        assertEquals(creditLimit, customer.getCreditLimit());
        verify(saveCustomerPort, times(1)).saveCustomer(customer);
    }

    @Test
    @DisplayName("Should complete KYC for customer")
    void shouldCompleteKYCForCustomer() {
        // Given
        Customer customer = createTestCustomer();
        LocalDate expiryDate = LocalDate.now().plusYears(1);

        CompleteKYCCommand command = CompleteKYCCommand.builder()
                .customerId(customer.getId().toString())
                .expiryDate(expiryDate)
                .completedBy(USER_ID)
                .build();

        when(loadCustomerPort.loadCustomerById(any(CustomerId.class)))
                .thenReturn(Optional.of(customer));
        when(saveCustomerPort.saveCustomer(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        customerService.completeKYC(command);

        // Then
        assertEquals(KYCStatus.COMPLETED, customer.getKycStatus());
        verify(saveCustomerPort, times(1)).saveCustomer(customer);
    }

    @Test
    @DisplayName("Should update risk rating")
    void shouldUpdateRiskRating() {
        // Given
        Customer customer = createActiveCustomer();
        BigDecimal riskScore = BigDecimal.valueOf(65);

        UpdateRiskRatingCommand command = UpdateRiskRatingCommand.builder()
                .customerId(customer.getId().toString())
                .riskRating(RiskRating.MEDIUM)
                .riskScore(riskScore)
                .reason("Medium risk assessment")
                .updatedBy(USER_ID)
                .build();

        when(loadCustomerPort.loadCustomerById(any(CustomerId.class)))
                .thenReturn(Optional.of(customer));
        when(saveCustomerPort.saveCustomer(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        customerService.updateRiskRating(command);

        // Then
        assertEquals(RiskRating.MEDIUM, customer.getRiskRating());
        assertEquals(riskScore, customer.getRiskScore());
        verify(saveCustomerPort, times(1)).saveCustomer(customer);
    }

    @Test
    @DisplayName("Should reject customer with reason")
    void shouldRejectCustomerWithReason() {
        // Given
        Customer customer = createTestCustomer();
        customer.submitForApproval(USER_ID);
        String rejectionReason = "Incomplete documentation";

        when(loadCustomerPort.loadCustomerById(any(CustomerId.class)))
                .thenReturn(Optional.of(customer));
        when(saveCustomerPort.saveCustomer(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        customerService.rejectCustomer(customer.getId().toString(), rejectionReason, USER_ID);

        // Then
        assertEquals(CustomerStatus.REJECTED, customer.getStatus());
        verify(saveCustomerPort, times(1)).saveCustomer(customer);
    }

    @Test
    @DisplayName("Should suspend customer")
    void shouldSuspendCustomer() {
        // Given
        Customer customer = createActiveCustomer();
        String suspensionReason = "Suspicious activity";

        when(loadCustomerPort.loadCustomerById(any(CustomerId.class)))
                .thenReturn(Optional.of(customer));
        when(saveCustomerPort.saveCustomer(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        customerService.suspendCustomer(customer.getId().toString(), suspensionReason, USER_ID);

        // Then
        assertEquals(CustomerStatus.SUSPENDED, customer.getStatus());
        verify(saveCustomerPort, times(1)).saveCustomer(customer);
    }

    @Test
    @DisplayName("Should throw exception when customer not found")
    void shouldThrowExceptionWhenCustomerNotFound() {
        // Given
        CustomerId nonExistentId = CustomerId.of(UUID.randomUUID());

        when(loadCustomerPort.loadCustomerById(any(CustomerId.class)))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(CustomerException.class, () ->
                customerService.getCustomerById(nonExistentId.toString())
        );
    }

    // Helper methods

    private Customer createTestCustomer() {
        return Customer.create(TENANT_ID, CUSTOMER_NAME, LEGAL_NAME, CustomerType.IMPORTER, EMAIL, USER_ID);
    }

    private Customer createActiveCustomer() {
        Customer customer = createTestCustomer();
        customer.submitForApproval(USER_ID);
        customer.completeKYC(LocalDate.now().plusYears(1), USER_ID);
        customer.updateComplianceStatus(ComplianceStatus.CLEARED, USER_ID);
        customer.approve(USER_ID);
        customer.activate(USER_ID);
        return customer;
    }
}


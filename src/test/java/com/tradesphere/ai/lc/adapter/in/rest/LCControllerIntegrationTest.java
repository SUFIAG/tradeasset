package com.tradesphere.ai.lc.adapter.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradesphere.ai.lc.adapter.in.web.LCController;
import com.tradesphere.ai.lc.application.service.LCService;
import com.tradesphere.ai.lc.domain.model.LCStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for LC REST API
 * Tests HTTP endpoints, request/response handling, validation
 */
@WebMvcTest(LCController.class)
@DisplayName("Letter of Credit API Integration Tests")
class LCControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LCService lcService;

    private static final String TENANT_ID = "TEST_TENANT";
    private static final UUID LC_ID = UUID.randomUUID();
    private static final UUID CUSTOMER_ID = UUID.randomUUID();
    private static final UUID BENEFICIARY_ID = UUID.randomUUID();

    @Test
    @DisplayName("POST /api/v1/lc - Should create LC application")
    void shouldCreateLCApplication() throws Exception {
        // Given
        var request = new CreateLCRequest(
            CUSTOMER_ID,
            BENEFICIARY_ID,
            new BigDecimal("100000.00"),
            "USD",
            LocalDate.now().plusMonths(6),
            "FOB",
            "Payment terms"
        );

        var response = new LCResponse(
            LC_ID,
            TENANT_ID,
            "LC-2024-00001234",
            CUSTOMER_ID,
            BENEFICIARY_ID,
            new BigDecimal("100000.00"),
            "USD",
            LCStatus.DRAFT,
            LocalDate.now(),
            LocalDate.now().plusMonths(6),
            null,
            null
        );

        when(lcService.createLC(any())).thenReturn(response);

        // When/Then
        mockMvc.perform(post("/api/v1/lc")
                .header("X-Tenant-ID", TENANT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(LC_ID.toString()))
            .andExpect(jsonPath("$.status").value("DRAFT"))
            .andExpect(jsonPath("$.amount").value(100000.00))
            .andExpect(jsonPath("$.currency").value("USD"));
    }

    @Test
    @DisplayName("GET /api/v1/lc/{id} - Should retrieve LC by ID")
    void shouldRetrieveLCById() throws Exception {
        // Given
        var response = new LCResponse(
            LC_ID,
            TENANT_ID,
            "LC-2024-00001234",
            CUSTOMER_ID,
            BENEFICIARY_ID,
            new BigDecimal("100000.00"),
            "USD",
            LCStatus.ISSUED,
            LocalDate.now(),
            LocalDate.now().plusMonths(6),
            LocalDate.now(),
            null
        );

        when(lcService.getLC(eq(LC_ID), eq(TENANT_ID))).thenReturn(response);

        // When/Then
        mockMvc.perform(get("/api/v1/lc/{id}", LC_ID)
                .header("X-Tenant-ID", TENANT_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(LC_ID.toString()))
            .andExpect(jsonPath("$.lcNumber").value("LC-2024-00001234"))
            .andExpect(jsonPath("$.status").value("ISSUED"));
    }

    @Test
    @DisplayName("POST /api/v1/lc/{id}/submit - Should submit LC for approval")
    void shouldSubmitLCForApproval() throws Exception {
        // Given
        when(lcService.submitLC(eq(LC_ID), eq(TENANT_ID), any())).thenReturn(true);

        // When/Then
        mockMvc.perform(post("/api/v1/lc/{id}/submit", LC_ID)
                .header("X-Tenant-ID", TENANT_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("POST /api/v1/lc/{id}/approve - Should approve LC")
    void shouldApproveLC() throws Exception {
        // Given
        when(lcService.approveLC(eq(LC_ID), eq(TENANT_ID), any())).thenReturn(true);

        // When/Then
        mockMvc.perform(post("/api/v1/lc/{id}/approve", LC_ID)
                .header("X-Tenant-ID", TENANT_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("POST /api/v1/lc/{id}/issue - Should issue LC")
    void shouldIssueLC() throws Exception {
        // Given
        when(lcService.issueLC(eq(LC_ID), eq(TENANT_ID), any())).thenReturn(true);

        // When/Then
        mockMvc.perform(post("/api/v1/lc/{id}/issue", LC_ID)
                .header("X-Tenant-ID", TENANT_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("POST /api/v1/lc/{id}/amendment - Should request amendment")
    void shouldRequestAmendment() throws Exception {
        // Given
        var amendmentRequest = new AmendmentRequest(
            new BigDecimal("150000.00"),
            LocalDate.now().plusMonths(9),
            "Increase amount and extend expiry"
        );

        when(lcService.requestAmendment(eq(LC_ID), eq(TENANT_ID), any(), any())).thenReturn(true);

        // When/Then
        mockMvc.perform(post("/api/v1/lc/{id}/amendment", LC_ID)
                .header("X-Tenant-ID", TENANT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(amendmentRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/lc - Should list LCs with pagination")
    void shouldListLCsWithPagination() throws Exception {
        // Given
        var lc1 = new LCResponse(
            UUID.randomUUID(), TENANT_ID, "LC-2024-001", CUSTOMER_ID, BENEFICIARY_ID,
            new BigDecimal("100000"), "USD", LCStatus.ISSUED,
            LocalDate.now(), LocalDate.now().plusMonths(6), LocalDate.now(), null
        );
        var lc2 = new LCResponse(
            UUID.randomUUID(), TENANT_ID, "LC-2024-002", CUSTOMER_ID, BENEFICIARY_ID,
            new BigDecimal("200000"), "USD", LCStatus.DRAFT,
            LocalDate.now(), LocalDate.now().plusMonths(6), null, null
        );

        when(lcService.listLCs(eq(TENANT_ID), any(), any())).thenReturn(List.of(lc1, lc2));

        // When/Then
        mockMvc.perform(get("/api/v1/lc")
                .header("X-Tenant-ID", TENANT_ID)
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].lcNumber").value("LC-2024-001"))
            .andExpect(jsonPath("$[1].lcNumber").value("LC-2024-002"));
    }

    @Test
    @DisplayName("POST /api/v1/lc/{id}/documents/present - Should present documents")
    void shouldPresentDocuments() throws Exception {
        // Given
        var documentRequest = new PresentDocumentsRequest(
            UUID.randomUUID(),
            List.of("Bill of Lading", "Commercial Invoice", "Packing List")
        );

        when(lcService.presentDocuments(eq(LC_ID), eq(TENANT_ID), any(), any())).thenReturn(true);

        // When/Then
        mockMvc.perform(post("/api/v1/lc/{id}/documents/present", LC_ID)
                .header("X-Tenant-ID", TENANT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(documentRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("POST /api/v1/lc - Should fail with invalid data")
    void shouldFailWithInvalidData() throws Exception {
        // Given - Invalid request (negative amount)
        var invalidRequest = new CreateLCRequest(
            CUSTOMER_ID,
            BENEFICIARY_ID,
            new BigDecimal("-1000.00"),
            "USD",
            LocalDate.now().plusMonths(6),
            "FOB",
            "Terms"
        );

        // When/Then
        mockMvc.perform(post("/api/v1/lc")
                .header("X-Tenant-ID", TENANT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/v1/lc/{id} - Should return 404 for non-existent LC")
    void shouldReturn404ForNonExistentLC() throws Exception {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(lcService.getLC(eq(nonExistentId), eq(TENANT_ID)))
            .thenThrow(new ResourceNotFoundException("LC not found"));

        // When/Then
        mockMvc.perform(get("/api/v1/lc/{id}", nonExistentId)
                .header("X-Tenant-ID", TENANT_ID))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/v1/lc - Should require tenant header")
    void shouldRequireTenantHeader() throws Exception {
        // Given
        var request = new CreateLCRequest(
            CUSTOMER_ID, BENEFICIARY_ID,
            new BigDecimal("100000"), "USD",
            LocalDate.now().plusMonths(6), "FOB", "Terms"
        );

        // When/Then
        mockMvc.perform(post("/api/v1/lc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    // Request/Response DTOs for testing
    record CreateLCRequest(
        UUID applicantId,
        UUID beneficiaryId,
        BigDecimal amount,
        String currency,
        LocalDate expiryDate,
        String incoterm,
        String paymentTerms
    ) {}

    record AmendmentRequest(
        BigDecimal newAmount,
        LocalDate newExpiryDate,
        String changes
    ) {}

    record PresentDocumentsRequest(
        UUID documentPackageId,
        List<String> documentTypes
    ) {}

    record LCResponse(
        UUID id,
        String tenantId,
        String lcNumber,
        UUID applicantId,
        UUID beneficiaryId,
        BigDecimal amount,
        String currency,
        LCStatus status,
        LocalDate applicationDate,
        LocalDate expiryDate,
        LocalDate issuedDate,
        LocalDate paymentDate
    ) {}

    static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }
}


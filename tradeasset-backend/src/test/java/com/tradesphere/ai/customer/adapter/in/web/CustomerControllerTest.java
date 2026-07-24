package com.tradesphere.ai.customer.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradesphere.ai.customer.adapter.in.web.dto.CreateCustomerRequest;
import com.tradesphere.ai.customer.application.service.CustomerService;
import com.tradesphere.ai.customer.domain.model.CustomerType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * REST API tests for Customer Controller
 * Tests HTTP endpoints with mocked security
 */
@WebMvcTest(CustomerController.class)
@DisplayName("Customer Controller API Tests")
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerService customerService;

    private static final UUID TENANT_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @Test
    @WithMockUser
    @DisplayName("Should create customer via POST /api/v1/customers")
    void shouldCreateCustomer() throws Exception {
        // Given
        CreateCustomerRequest request = new CreateCustomerRequest();
        request.setName("Test Customer");
        request.setEmail("test@customer.com");
        request.setType(CustomerType.IMPORTER);

        // When & Then
        mockMvc.perform(post("/api/v1/customers")
                        .with(csrf())
                        .header("X-Tenant-ID", TENANT_ID.toString())
                        .header("X-User-ID", USER_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Test Customer"))
                .andExpect(jsonPath("$.email").value("test@customer.com"));
    }

    @Test
    @WithMockUser
    @DisplayName("Should fail validation when name is missing")
    void shouldFailValidationWhenNameIsMissing() throws Exception {
        // Given
        CreateCustomerRequest request = new CreateCustomerRequest();
        request.setEmail("test@customer.com");
        request.setType(CustomerType.IMPORTER);

        // When & Then
        mockMvc.perform(post("/api/v1/customers")
                        .with(csrf())
                        .header("X-Tenant-ID", TENANT_ID.toString())
                        .header("X-User-ID", USER_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("Should get customer by ID")
    void shouldGetCustomerById() throws Exception {
        // Given
        UUID customerId = UUID.randomUUID();

        // When & Then
        mockMvc.perform(get("/api/v1/customers/{id}", customerId)
                        .header("X-Tenant-ID", TENANT_ID.toString()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    @DisplayName("Should approve customer")
    void shouldApproveCustomer() throws Exception {
        // Given
        UUID customerId = UUID.randomUUID();

        // When & Then
        mockMvc.perform(post("/api/v1/customers/{id}/approve", customerId)
                        .with(csrf())
                        .header("X-Tenant-ID", TENANT_ID.toString())
                        .header("X-User-ID", USER_ID.toString()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return 401 when not authenticated")
    void shouldReturn401WhenNotAuthenticated() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/customers"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 400 when tenant header is missing")
    void shouldReturn400WhenTenantHeaderIsMissing() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/customers"))
                .andExpect(status().isBadRequest());
    }
}


package com.tradesphere.ai.customer.adapter.in.web;

import com.tradesphere.ai.customer.adapter.in.web.dto.*;
import com.tradesphere.ai.customer.adapter.in.web.mapper.CustomerDTOMapper;
import com.tradesphere.ai.customer.application.port.in.*;
import com.tradesphere.ai.customer.domain.model.Customer;
import com.tradesphere.ai.customer.domain.model.CustomerException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller for Customer Management
 * Provides endpoints for customer lifecycle management
 */
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Customer Management", description = "APIs for managing customers in trade finance")
public class CustomerController {

    private final CreateCustomerUseCase createCustomerUseCase;
    private final GetCustomerUseCase getCustomerUseCase;
    private final UpdateCustomerUseCase updateCustomerUseCase;
    private final ApproveCustomerUseCase approveCustomerUseCase;
    private final RejectCustomerUseCase rejectCustomerUseCase;
    private final SuspendCustomerUseCase suspendCustomerUseCase;
    private final CompleteKYCUseCase completeKYCUseCase;
    private final SetCreditLimitUseCase setCreditLimitUseCase;
    private final UpdateRiskRatingUseCase updateRiskRatingUseCase;
    private final SubmitForApprovalUseCase submitForApprovalUseCase;
    private final ListCustomersUseCase listCustomersUseCase;
    private final CustomerDTOMapper dtoMapper;

    @PostMapping
    @Operation(summary = "Create a new customer", description = "Creates a new customer in the system")
    public ResponseEntity<CustomerResponse> createCustomer(
            @Valid @RequestBody CreateCustomerRequest request,
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @RequestHeader("X-User-Id") UUID userId) {

        log.info("Creating customer: {}", request.getName());

        Customer customer = createCustomerUseCase.createCustomer(
                dtoMapper.toCommand(request, tenantId, userId)
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(dtoMapper.toResponse(customer));
    }

    @GetMapping("/{customerId}")
    @Operation(summary = "Get customer by ID", description = "Retrieves customer details by ID")
    public ResponseEntity<CustomerResponse> getCustomer(@PathVariable String customerId) {
        log.info("Getting customer: {}", customerId);

        return getCustomerUseCase.getCustomerById(customerId)
                .map(customer -> ResponseEntity.ok(dtoMapper.toResponse(customer)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/number/{customerNumber}")
    @Operation(summary = "Get customer by number", description = "Retrieves customer details by customer number")
    public ResponseEntity<CustomerResponse> getCustomerByNumber(@PathVariable String customerNumber) {
        log.info("Getting customer by number: {}", customerNumber);

        return getCustomerUseCase.getCustomerByNumber(customerNumber)
                .map(customer -> ResponseEntity.ok(dtoMapper.toResponse(customer)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{customerId}")
    @Operation(summary = "Update customer", description = "Updates customer basic information")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable String customerId,
            @Valid @RequestBody UpdateCustomerRequest request,
            @RequestHeader("X-User-Id") UUID userId) {

        log.info("Updating customer: {}", customerId);

        Customer customer = updateCustomerUseCase.updateCustomer(
                dtoMapper.toCommand(customerId, request, userId)
        );

        return ResponseEntity.ok(dtoMapper.toResponse(customer));
    }

    @PostMapping("/{customerId}/submit")
    @Operation(summary = "Submit customer for approval", description = "Submits customer for approval workflow")
    public ResponseEntity<CustomerResponse> submitForApproval(
            @PathVariable String customerId,
            @RequestHeader("X-User-Id") UUID userId) {

        log.info("Submitting customer for approval: {}", customerId);

        Customer customer = submitForApprovalUseCase.submitForApproval(customerId, userId);

        return ResponseEntity.ok(dtoMapper.toResponse(customer));
    }

    @PostMapping("/{customerId}/approve")
    @Operation(summary = "Approve customer", description = "Approves a customer application")
    public ResponseEntity<CustomerResponse> approveCustomer(
            @PathVariable String customerId,
            @RequestHeader("X-User-Id") UUID userId) {

        log.info("Approving customer: {}", customerId);

        Customer customer = approveCustomerUseCase.approveCustomer(
                ApproveCustomerCommand.builder()
                        .customerId(customerId)
                        .approvedBy(userId)
                        .build()
        );

        return ResponseEntity.ok(dtoMapper.toResponse(customer));
    }

    @PostMapping("/{customerId}/activate")
    @Operation(summary = "Activate customer", description = "Activates an approved customer")
    public ResponseEntity<CustomerResponse> activateCustomer(
            @PathVariable String customerId,
            @RequestHeader("X-User-Id") UUID userId) {

        log.info("Activating customer: {}", customerId);

        Customer customer = approveCustomerUseCase.activateCustomer(customerId, userId);

        return ResponseEntity.ok(dtoMapper.toResponse(customer));
    }

    @PostMapping("/{customerId}/reject")
    @Operation(summary = "Reject customer", description = "Rejects a customer application")
    public ResponseEntity<CustomerResponse> rejectCustomer(
            @PathVariable String customerId,
            @RequestParam String reason,
            @RequestHeader("X-User-Id") UUID userId) {

        log.info("Rejecting customer: {}", customerId);

        Customer customer = rejectCustomerUseCase.rejectCustomer(customerId, reason, userId);

        return ResponseEntity.ok(dtoMapper.toResponse(customer));
    }

    @PostMapping("/{customerId}/suspend")
    @Operation(summary = "Suspend customer", description = "Suspends an active customer")
    public ResponseEntity<CustomerResponse> suspendCustomer(
            @PathVariable String customerId,
            @RequestParam String reason,
            @RequestHeader("X-User-Id") UUID userId) {

        log.info("Suspending customer: {}", customerId);

        Customer customer = suspendCustomerUseCase.suspendCustomer(customerId, reason, userId);

        return ResponseEntity.ok(dtoMapper.toResponse(customer));
    }

    @PostMapping("/{customerId}/reactivate")
    @Operation(summary = "Reactivate customer", description = "Reactivates a suspended customer")
    public ResponseEntity<CustomerResponse> reactivateCustomer(
            @PathVariable String customerId,
            @RequestHeader("X-User-Id") UUID userId) {

        log.info("Reactivating customer: {}", customerId);

        Customer customer = suspendCustomerUseCase.reactivateCustomer(customerId, userId);

        return ResponseEntity.ok(dtoMapper.toResponse(customer));
    }

    @PostMapping("/{customerId}/kyc/complete")
    @Operation(summary = "Complete KYC", description = "Marks KYC as completed for a customer")
    public ResponseEntity<CustomerResponse> completeKYC(
            @PathVariable String customerId,
            @Valid @RequestBody CompleteKYCRequest request,
            @RequestHeader("X-User-Id") UUID userId) {

        log.info("Completing KYC for customer: {}", customerId);

        Customer customer = completeKYCUseCase.completeKYC(
                dtoMapper.toCommand(customerId, request, userId)
        );

        return ResponseEntity.ok(dtoMapper.toResponse(customer));
    }

    @PostMapping("/{customerId}/credit-limit")
    @Operation(summary = "Set credit limit", description = "Sets credit limit for a customer")
    public ResponseEntity<CustomerResponse> setCreditLimit(
            @PathVariable String customerId,
            @Valid @RequestBody SetCreditLimitRequest request,
            @RequestHeader("X-User-Id") UUID userId) {

        log.info("Setting credit limit for customer: {}", customerId);

        Customer customer = setCreditLimitUseCase.setCreditLimit(
                dtoMapper.toCommand(customerId, request, userId)
        );

        return ResponseEntity.ok(dtoMapper.toResponse(customer));
    }

    @PostMapping("/{customerId}/risk-rating")
    @Operation(summary = "Update customer risk rating", description = "Updates the risk rating of a customer")
    public ResponseEntity<CustomerResponse> updateRiskRating(
            @PathVariable String customerId,
            @Valid @RequestBody UpdateRiskRatingRequest request,
            @RequestHeader("X-User-Id") UUID userId) {

        log.info("Updating risk rating for customer: {}", customerId);

        Customer customer = updateRiskRatingUseCase.updateRiskRating(
                dtoMapper.toCommand(customerId, request, userId)
        );

        return ResponseEntity.ok(dtoMapper.toResponse(customer));
    }

    @GetMapping
    @Operation(summary = "List all customers", description = "Lists all customers with pagination")
    public ResponseEntity<Page<CustomerResponse>> listCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction) {

        log.info("Listing customers - page: {}, size: {}, sortBy: {}", page, size, sortBy);

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<Customer> customers = listCustomersUseCase.listCustomers(pageable);
        Page<CustomerResponse> response = customers.map(dtoMapper::toResponse);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Search customers", description = "Search customers by name, email, or customer number")
    public ResponseEntity<Page<CustomerResponse>> searchCustomers(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("Searching customers with query: {}", query);

        Pageable pageable = PageRequest.of(page, size);
        Page<Customer> customers = listCustomersUseCase.searchCustomers(query, pageable);
        Page<CustomerResponse> response = customers.map(dtoMapper::toResponse);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-status/{status}")
    @Operation(summary = "List customers by status", description = "Lists customers filtered by status")
    public ResponseEntity<Page<CustomerResponse>> listCustomersByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("Listing customers by status: {}", status);

        Pageable pageable = PageRequest.of(page, size);
        Page<Customer> customers = listCustomersUseCase.listCustomersByStatus(status, pageable);
        Page<CustomerResponse> response = customers.map(dtoMapper::toResponse);

        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(CustomerException.class)
    public ResponseEntity<ErrorResponse> handleCustomerException(CustomerException ex) {
        log.error("Customer exception: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                "CUSTOMER_ERROR",
                ex.getMessage(),
                System.currentTimeMillis()
        );
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Invalid argument: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                "INVALID_ARGUMENT",
                ex.getMessage(),
                System.currentTimeMillis()
        );
        return ResponseEntity.badRequest().body(error);
    }

    private record ErrorResponse(String code, String message, long timestamp) {}
}

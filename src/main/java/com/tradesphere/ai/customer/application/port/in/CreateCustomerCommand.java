package com.tradesphere.ai.customer.application.port.in;

import com.tradesphere.ai.customer.domain.model.CustomerType;
import com.tradesphere.ai.shared.domain.valueobject.Address;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

/**
 * Command to create a new customer
 */
@Value
@Builder
public class CreateCustomerCommand {
    UUID tenantId;
    String name;
    String legalName;
    CustomerType type;
    String category;
    String industry;
    String businessRegistrationNumber;
    String taxId;
    String email;
    String phone;
    String website;
    Address address;
    UUID createdBy;
}


package com.tradesphere.ai.customer.application.port.in;

import com.tradesphere.ai.shared.domain.valueobject.Address;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

/**
 * Command to update customer basic information
 */
@Value
@Builder
public class UpdateCustomerCommand {
    String customerId;
    String name;
    String legalName;
    String email;
    String phone;
    String website;
    Address address;
    UUID updatedBy;
}


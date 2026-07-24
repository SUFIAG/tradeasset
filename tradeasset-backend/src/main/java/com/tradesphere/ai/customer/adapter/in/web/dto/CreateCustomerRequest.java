package com.tradesphere.ai.customer.adapter.in.web.dto;

import com.tradesphere.ai.customer.domain.model.CustomerType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * REST DTO for creating a customer
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCustomerRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Legal name is required")
    private String legalName;

    @NotNull(message = "Customer type is required")
    private CustomerType type;

    private String category;

    private String industry;

    private String businessRegistrationNumber;

    private String taxId;

    private LocalDate incorporationDate;

    private String incorporationCountry;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    private String phone;

    private String website;

    // Address fields
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
    private String country;
}


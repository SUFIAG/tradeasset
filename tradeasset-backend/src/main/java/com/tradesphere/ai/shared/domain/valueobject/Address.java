package com.tradesphere.ai.shared.domain.valueobject;

import com.tradesphere.ai.shared.domain.ValueObject;
import lombok.Value;

/**
 * Address Value Object
 * Represents a physical address
 */
@Value
public class Address implements ValueObject {

    String line1;
    String line2;
    String city;
    String state;
    String country;  // ISO 3166-1 alpha-2 (e.g., "US", "GB", "AE")
    String postalCode;

    private Address(
            String line1,
            String line2,
            String city,
            String state,
            String country,
            String postalCode) {
        if (line1 == null || line1.trim().isEmpty()) {
            throw new IllegalArgumentException("Address line 1 is required");
        }
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("City is required");
        }
        if (country == null || country.trim().isEmpty()) {
            throw new IllegalArgumentException("Country is required");
        }
        if (country.length() != 2) {
            throw new IllegalArgumentException("Country must be ISO 3166-1 alpha-2 code (2 characters)");
        }

        this.line1 = line1.trim();
        this.line2 = line2 != null ? line2.trim() : null;
        this.city = city.trim();
        this.state = state != null ? state.trim() : null;
        this.country = country.toUpperCase();
        this.postalCode = postalCode != null ? postalCode.trim() : null;
    }

    public static Address of(
            String line1,
            String city,
            String country,
            String postalCode) {
        return new Address(line1, null, city, null, country, postalCode);
    }

    public static Address of(
            String line1,
            String line2,
            String city,
            String state,
            String country,
            String postalCode) {
        return new Address(line1, line2, city, state, country, postalCode);
    }

    /**
     * Get full address as a single string
     */
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        sb.append(line1);
        if (line2 != null && !line2.isEmpty()) {
            sb.append(", ").append(line2);
        }
        sb.append(", ").append(city);
        if (state != null && !state.isEmpty()) {
            sb.append(", ").append(state);
        }
        if (postalCode != null && !postalCode.isEmpty()) {
            sb.append(" ").append(postalCode);
        }
        sb.append(", ").append(country);
        return sb.toString();
    }

    /**
     * Check if address is in a specific country
     */
    public boolean isInCountry(String countryCode) {
        return this.country.equalsIgnoreCase(countryCode);
    }

    @Override
    public String toString() {
        return getFullAddress();
    }
}


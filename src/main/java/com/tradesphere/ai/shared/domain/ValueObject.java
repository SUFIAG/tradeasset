package com.tradesphere.ai.shared.domain;

import java.io.Serializable;

/**
 * Marker interface for Value Objects
 *
 * Value Objects are immutable objects that describe characteristics or attributes of things
 * They have no identity and are defined only by their attributes
 *
 * Key characteristics:
 * 1. Immutable - once created, cannot be changed
 * 2. No identity - equality based on attributes, not identity
 * 3. Side-effect free behavior
 * 4. Must implement equals() and hashCode() based on all attributes
 *
 * Examples: Money, Address, DateRange, Email, Phone Number
 */
public interface ValueObject extends Serializable {
    // Marker interface - no methods required
    // Implementations should be immutable and implement equals/hashCode
}


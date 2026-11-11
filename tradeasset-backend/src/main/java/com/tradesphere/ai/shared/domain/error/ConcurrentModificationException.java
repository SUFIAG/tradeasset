package com.tradesphere.ai.shared.domain.error;

import com.tradesphere.ai.shared.domain.DomainException;

/**
 * The record changed between read and write (optimistic lock failure). Rendered as 409 with code
 * CONCURRENT_MODIFICATION; the client reloads and retries.
 */
public class ConcurrentModificationException extends DomainException {

    public ConcurrentModificationException(String resourceType, Object id) {
        super(resourceType + " " + id + " was modified concurrently", "CONCURRENT_MODIFICATION");
    }
}

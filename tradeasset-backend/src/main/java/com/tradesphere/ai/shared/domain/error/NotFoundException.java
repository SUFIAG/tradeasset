package com.tradesphere.ai.shared.domain.error;

import com.tradesphere.ai.shared.domain.DomainException;

/**
 * A record does not exist in the caller's tenant. Rendered as 404 with code RESOURCE_NOT_FOUND.
 * A record in another tenant is reported exactly the same way (REQ-SEC-008).
 */
public class NotFoundException extends DomainException {

    private final String resourceType;

    public NotFoundException(String resourceType, Object id) {
        super(resourceType + " not found: " + id, "RESOURCE_NOT_FOUND");
        this.resourceType = resourceType;
    }

    public String getResourceType() {
        return resourceType;
    }
}

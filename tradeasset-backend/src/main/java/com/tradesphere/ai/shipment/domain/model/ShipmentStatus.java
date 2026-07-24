package com.tradesphere.ai.shipment.domain.model;

public enum ShipmentStatus {
    CREATED,
    READY,
    IN_TRANSIT,
    ARRIVED,
    CUSTOMS_CLEARED,
    DELIVERED,
    CANCELLED;
    
    public boolean isTerminal() {
        return this == DELIVERED || this == CANCELLED;
    }
    
    public boolean isActive() {
        return this == IN_TRANSIT || this == ARRIVED || this == CUSTOMS_CLEARED;
    }
}


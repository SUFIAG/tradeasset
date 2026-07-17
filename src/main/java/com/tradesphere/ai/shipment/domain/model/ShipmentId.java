package com.tradesphere.ai.shipment.domain.model;

import java.util.UUID;

public record ShipmentId(UUID value) {
    public static ShipmentId of(UUID value) {
        return new ShipmentId(value);
    }

    public static ShipmentId generate() {
        return new ShipmentId(UUID.randomUUID());
    }

    public UUID getValue() {
        return value;
    }
}


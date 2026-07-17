package com.tradesphere.ai.shipment.domain.model;

public enum TransportMode {
    SEA,
    AIR,
    ROAD,
    RAIL,
    MULTIMODAL;

    public int getTypicalDays() {
        return switch (this) {
            case AIR -> 3;
            case ROAD -> 7;
            case RAIL -> 10;
            case SEA -> 30;
            case MULTIMODAL -> 20;
        };
    }
}


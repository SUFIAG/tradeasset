package com.tradesphere.ai.bankguarantee.domain.model;

import java.util.UUID;

public record BankGuaranteeId(UUID value) {

    public static BankGuaranteeId of(UUID value) {
        return new BankGuaranteeId(value);
    }

    public static BankGuaranteeId generate() {
        return new BankGuaranteeId(UUID.randomUUID());
    }

    public UUID getValue() {
        return value;
    }
}


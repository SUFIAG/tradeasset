package com.tradesphere.ai.customer.domain.model;

/**
 * Customer Type Enumeration
 * Defines the different types of customers in the trade finance ecosystem
 */
public enum CustomerType {
    /**
     * Importer - Buys goods from foreign suppliers
     */
    IMPORTER,

    /**
     * Exporter - Sells goods to foreign buyers
     */
    EXPORTER,

    /**
     * Buyer - General buyer in trade transactions
     */
    BUYER,

    /**
     * Seller - General seller in trade transactions
     */
    SELLER,

    /**
     * Bank - Financial institution
     */
    BANK,

    /**
     * Beneficiary - Receives payment under LC
     */
    BENEFICIARY,

    /**
     * Freight Forwarder - Logistics service provider
     */
    FREIGHT_FORWARDER,

    /**
     * Customs Broker - Handles customs clearance
     */
    CUSTOMS_BROKER,

    /**
     * Insurance Company - Provides cargo insurance
     */
    INSURANCE_COMPANY,

    /**
     * Other - Any other type of customer
     */
    OTHER;

    public boolean isImporter() {
        return this == IMPORTER || this == BUYER;
    }

    public boolean isExporter() {
        return this == EXPORTER || this == SELLER || this == BENEFICIARY;
    }

    public boolean isFinancialInstitution() {
        return this == BANK;
    }

    public boolean isServiceProvider() {
        return this == FREIGHT_FORWARDER || this == CUSTOMS_BROKER || this == INSURANCE_COMPANY;
    }
}


package com.tradesphere.ai.scf.domain.model;

public enum SCFType {
    INVOICE_FINANCING,          // Supplier sells invoices to financier
    REVERSE_FACTORING,          // Buyer-centric early payment program
    PURCHASE_ORDER_FINANCING,   // Financing based on confirmed POs
    INVENTORY_FINANCING,        // Financing against inventory
    RECEIVABLES_DISCOUNTING;    // Discounting of receivables portfolio
    
    public String getDescription() {
        return switch (this) {
            case INVOICE_FINANCING -> "Invoice Financing/Factoring";
            case REVERSE_FACTORING -> "Reverse Factoring/Supplier Finance";
            case PURCHASE_ORDER_FINANCING -> "Purchase Order Financing";
            case INVENTORY_FINANCING -> "Inventory Financing";
            case RECEIVABLES_DISCOUNTING -> "Receivables Discounting";
        };
    }
}


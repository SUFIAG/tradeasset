package com.tradesphere.ai.lc.domain.model;

/**
 * International Commercial Terms (Incoterms) 2020
 * Defines responsibilities of buyers and sellers in international trade
 */
public enum Incoterm {
    
    // Any mode of transport
    /**
     * Ex Works - Seller makes goods available at their premises
     */
    EXW("Ex Works"),
    
    /**
     * Free Carrier - Seller delivers goods to carrier named by buyer
     */
    FCA("Free Carrier"),
    
    /**
     * Carriage Paid To - Seller pays for carriage to named destination
     */
    CPT("Carriage Paid To"),
    
    /**
     * Carriage and Insurance Paid To - Seller pays for carriage and insurance
     */
    CIP("Carriage and Insurance Paid To"),
    
    /**
     * Delivered at Place - Seller delivers when goods are at buyer's disposal
     */
    DAP("Delivered at Place"),
    
    /**
     * Delivered at Place Unloaded - Seller delivers and unloads at destination
     */
    DPU("Delivered at Place Unloaded"),
    
    /**
     * Delivered Duty Paid - Seller delivers goods cleared for import
     */
    DDP("Delivered Duty Paid"),
    
    // Sea and inland waterway transport only
    /**
     * Free Alongside Ship - Seller delivers goods alongside vessel
     */
    FAS("Free Alongside Ship"),
    
    /**
     * Free On Board - Seller delivers goods on board vessel
     */
    FOB("Free On Board"),
    
    /**
     * Cost and Freight - Seller pays costs and freight to destination
     */
    CFR("Cost and Freight"),
    
    /**
     * Cost, Insurance and Freight - Seller pays costs, insurance and freight
     */
    CIF("Cost, Insurance and Freight");
    
    private final String description;
    
    Incoterm(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}


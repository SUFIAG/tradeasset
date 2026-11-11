package com.tradesphere.ai.shared.domain.trade;

import java.util.List;

/**
 * Stable codes for every record type that can appear in the audit trail, the movement journal,
 * the approval register, the document store or a workflow task. Stored in VARCHAR(20) columns,
 * so a code is never longer than 20 characters. Adding a code is additive; never rename one.
 */
public final class EntityTypes {

    public static final String LC = "LC";
    public static final String BG = "BG";
    public static final String COLLECTION = "DC";
    public static final String SCF = "SCF";
    public static final String SHIPMENT = "SHIPMENT";
    public static final String CUSTOMER = "CUSTOMER";
    public static final String USER = "USER";
    public static final String CREDIT_LIMIT = "CREDIT_LIMIT";
    public static final String RISK_LIMIT = "RISK_LIMIT";
    public static final String LIMIT_EXCESS = "LIMIT_EXCESS";
    public static final String RISK_ASSESSMENT = "RISK_ASSESSMENT";
    public static final String SCREENING = "SCREENING";
    public static final String SCREENING_HIT = "SCREENING_HIT";
    public static final String TBML_ALERT = "TBML_ALERT";
    public static final String DOCUMENT = "DOCUMENT";
    public static final String WORKFLOW_TASK = "WORKFLOW_TASK";
    public static final String FX_RATE = "FX_RATE";

    /** Instruments that carry credit exposure and appear on the dashboard. */
    public static final List<String> INSTRUMENTS = List.of(LC, BG, COLLECTION, SCF);

    private EntityTypes() {
    }
}

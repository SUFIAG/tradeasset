package com.tradesphere.ai.shared.domain.journal;

/**
 * Account codes of the movement journal (REQ-MON-006). Stored in VARCHAR(30).
 *
 * <p>Sign convention: a positive movement increases the balance of the account, a negative one
 * decreases it. Every balance is {@code SUM(amount)} of the account's movements; nothing else is
 * authoritative.</p>
 */
public final class LedgerAccounts {

    /**
     * Credit consumed from the customer's limits by an instrument: committed, utilised or
     * contingent exposure (REQ-CUS-006, REQ-RSK-002). Posted with the customer ID set.
     * Issuance posts +amount; payment, claim settlement, release, expiry, cancellation and
     * repayment post the matching negative amount.
     */
    public static final String CREDIT_EXPOSURE = "CREDIT_EXPOSURE";

    /** Undrawn amount still available under an LC (REQ-LC-009). */
    public static final String LC_AVAILABLE = "LC_AVAILABLE";

    /** Outstanding (claimable) amount of a bank guarantee (REQ-BG-005). */
    public static final String BG_OUTSTANDING = "BG_OUTSTANDING";

    /** Outstanding principal of a supply-chain finance facility (REQ-SCF-007). */
    public static final String SCF_OUTSTANDING = "SCF_OUTSTANDING";

    /** Principal amount of a documentary collection still to be settled. */
    public static final String DC_OUTSTANDING = "DC_OUTSTANDING";

    /** Amounts actually paid out against an instrument (drawings, claim payments, disbursements). */
    public static final String SETTLED = "SETTLED";

    /** Fee income: commissions, bank charges, discount. Never netted into a principal (rule 8). */
    public static final String FEE = "FEE";

    /** VAT on fees, always a separate entry. */
    public static final String VAT = "VAT";

    private LedgerAccounts() {
    }
}

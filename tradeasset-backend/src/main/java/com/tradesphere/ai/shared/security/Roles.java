package com.tradesphere.ai.shared.security;

import java.util.List;

/** BRD section 1.1 role codes. Spring authorities carry them with the ROLE_ prefix. */
public final class Roles {

    public static final String MAKER = "MAKER";
    public static final String CHECKER = "CHECKER";
    public static final String APPROVER = "APPROVER";
    public static final String COMPLIANCE_OFFICER = "COMPLIANCE_OFFICER";
    public static final String RISK_OFFICER = "RISK_OFFICER";
    public static final String OPERATIONS = "OPERATIONS";
    public static final String AUDITOR = "AUDITOR";
    public static final String SYSTEM_ADMIN = "SYSTEM_ADMIN";
    public static final String CUSTOMER_USER = "CUSTOMER_USER";

    /** Roles for which MFA is mandatory (REQ-SEC-007). */
    public static final List<String> MFA_MANDATORY = List.of(APPROVER, COMPLIANCE_OFFICER, SYSTEM_ADMIN);

    public static final List<String> ALL = List.of(MAKER, CHECKER, APPROVER, COMPLIANCE_OFFICER, RISK_OFFICER,
            OPERATIONS, AUDITOR, SYSTEM_ADMIN, CUSTOMER_USER);

    private Roles() {
    }
}

package com.tradesphere.ai.lc.domain.model;

import com.tradesphere.ai.shared.domain.DomainException;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Typed business error for the LC module. The {@link #getErrorCode() code} is part of the API
 * contract: the frontend maps it to a localised message. Never change the meaning of an existing code.
 */
public class LcException extends DomainException {

    /** How the error should surface over HTTP; kept free of any web dependency. */
    public enum Kind { NOT_FOUND, CONFLICT, UNPROCESSABLE, FORBIDDEN }

    private final Kind kind;
    private final transient Map<String, Object> properties;

    public LcException(String code, Kind kind, String message, Map<String, Object> properties) {
        super(message, code);
        this.kind = kind;
        this.properties = properties == null ? Map.of() : Map.copyOf(properties);
    }

    public Kind getKind() {
        return kind;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public static LcException notFound() {
        return new LcException("LC_NOT_FOUND", Kind.NOT_FOUND, "Letter of credit not found", null);
    }

    public static LcException subResourceNotFound(String what) {
        return new LcException("LC_" + what + "_NOT_FOUND", Kind.NOT_FOUND, what.toLowerCase().replace('_', ' ') + " not found", null);
    }

    public static LcException illegalTransition(LcStatus current, LcAction action,
                                                Collection<LcAction> legalActions,
                                                Collection<LcStatus> legalNextStates) {
        return new LcException("LC_ILLEGAL_TRANSITION", Kind.CONFLICT,
                "Action " + action + " is not permitted while the letter of credit is " + current,
                Map.of("currentState", current.name(),
                        "attemptedAction", action.name(),
                        "legalActions", legalActions.stream().map(Enum::name).sorted().toList(),
                        "legalNextStates", legalNextStates.stream().map(Enum::name).toList()));
    }

    public static LcException rule(String code, String message) {
        return new LcException(code, Kind.UNPROCESSABLE, message, null);
    }

    public static LcException rule(String code, String message, Map<String, Object> properties) {
        return new LcException(code, Kind.UNPROCESSABLE, message, properties);
    }

    public static LcException conflict(String code, String message) {
        return new LcException(code, Kind.CONFLICT, message, null);
    }

    /** Four-eyes violation: the same person may not make and check (REQ-MKR-002). */
    public static LcException fourEyes(String message) {
        return new LcException("LC_FOUR_EYES_VIOLATION", Kind.FORBIDDEN, message, null);
    }

    public static LcException issuanceBlocked(List<String> reasons) {
        return new LcException("LC_ISSUANCE_BLOCKED", Kind.UNPROCESSABLE,
                "Issuance is blocked: " + String.join("; ", reasons), Map.of("reasons", List.copyOf(reasons)));
    }
}

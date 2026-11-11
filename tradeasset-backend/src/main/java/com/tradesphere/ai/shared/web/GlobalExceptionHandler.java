package com.tradesphere.ai.shared.web;

import com.tradesphere.ai.lc.domain.model.LcException;
import com.tradesphere.ai.shared.domain.DomainException;
import com.tradesphere.ai.shared.domain.error.BusinessRuleViolationException;
import com.tradesphere.ai.shared.domain.error.ConcurrentModificationException;
import com.tradesphere.ai.shared.domain.error.ConflictException;
import com.tradesphere.ai.shared.domain.error.ForbiddenActionException;
import com.tradesphere.ai.shared.domain.error.IllegalStateTransitionException;
import com.tradesphere.ai.shared.domain.error.NotFoundException;
import com.tradesphere.ai.shared.domain.error.ValidationException;
import com.tradesphere.ai.shared.domain.valueobject.Money;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Turns every exception into an RFC 7807 problem with a stable code. Never returns a stack
 * trace, a SQL fragment, an internal class name or raw text from an external system.
 *
 * <p>Error code catalogue (generic codes; modules add their own):</p>
 * <ul>
 *   <li>400 VALIDATION_FAILED, MALFORMED_REQUEST, CURRENCY_MISMATCH</li>
 *   <li>401 AUTHENTICATION_REQUIRED, AUTH_TOKEN_EXPIRED</li>
 *   <li>403 ACCESS_DENIED, FOUR_EYES_VIOLATION and module codes</li>
 *   <li>404 RESOURCE_NOT_FOUND</li>
 *   <li>409 ILLEGAL_STATE_TRANSITION, CONCURRENT_MODIFICATION, DUPLICATE_RECORD and module codes</li>
 *   <li>413 FILE_TOO_LARGE</li>
 *   <li>422 business rule codes</li>
 *   <li>500 INTERNAL_ERROR</li>
 * </ul>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ValidationException.class)
    public ProblemDetail validation(ValidationException e) {
        return Problems.of(HttpStatus.BAD_REQUEST, e.getErrorCode(), e.getMessage(),
                Map.of("fieldErrors", List.of(Map.of("field", e.getField() == null ? "" : e.getField(),
                        "message", e.getMessage()))));
    }

    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail notFound(NotFoundException e) {
        // Same answer whether the record does not exist or belongs to another tenant (REQ-SEC-008).
        return Problems.of(HttpStatus.NOT_FOUND, e.getErrorCode(), e.getResourceType() + " not found",
                Map.of("resourceType", e.getResourceType()));
    }

    @ExceptionHandler(IllegalStateTransitionException.class)
    public ProblemDetail illegalTransition(IllegalStateTransitionException e) {
        return Problems.of(HttpStatus.CONFLICT, e.getErrorCode(), e.getMessage(), Map.of(
                "resourceType", e.getResourceType(),
                "currentState", e.getCurrentState(),
                "attemptedAction", e.getAttemptedAction(),
                "legalActions", e.getLegalNextActions()));
    }

    @ExceptionHandler(ConflictException.class)
    public ProblemDetail conflict(ConflictException e) {
        return Problems.of(HttpStatus.CONFLICT, e.getErrorCode(), e.getMessage(), e.getProperties());
    }

    @ExceptionHandler(ConcurrentModificationException.class)
    public ProblemDetail concurrent(ConcurrentModificationException e) {
        return Problems.of(HttpStatus.CONFLICT, e.getErrorCode(), "The record was changed by someone else. Reload and try again.");
    }

    @ExceptionHandler(ForbiddenActionException.class)
    public ProblemDetail forbiddenAction(ForbiddenActionException e) {
        return Problems.of(HttpStatus.FORBIDDEN, e.getErrorCode(), e.getMessage());
    }

    @ExceptionHandler(BusinessRuleViolationException.class)
    public ProblemDetail businessRule(BusinessRuleViolationException e) {
        return Problems.of(HttpStatus.UNPROCESSABLE_ENTITY, e.getErrorCode(), e.getMessage(), e.getProperties());
    }

    @ExceptionHandler(LcException.class)
    public ProblemDetail lc(LcException e) {
        HttpStatus status = switch (e.getKind()) {
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case CONFLICT -> HttpStatus.CONFLICT;
            case FORBIDDEN -> HttpStatus.FORBIDDEN;
            case UNPROCESSABLE -> HttpStatus.UNPROCESSABLE_ENTITY;
        };
        return Problems.of(status, e.getErrorCode(), e.getMessage(), e.getProperties());
    }

    @ExceptionHandler(Money.CurrencyMismatchException.class)
    public ProblemDetail currencyMismatch(Money.CurrencyMismatchException e) {
        return Problems.of(HttpStatus.BAD_REQUEST, "CURRENCY_MISMATCH", e.getMessage(),
                Map.of("currencies", List.of(e.getLeft(), e.getRight())));
    }

    /** Any other typed domain error is a business rule rejection. */
    @ExceptionHandler(DomainException.class)
    public ProblemDetail domain(DomainException e) {
        return Problems.of(HttpStatus.UNPROCESSABLE_ENTITY, safeCode(e.getErrorCode()), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail beanValidation(MethodArgumentNotValidException e) {
        List<Map<String, String>> fields = e.getBindingResult().getFieldErrors().stream()
                .map(GlobalExceptionHandler::fieldError).toList();
        List<Map<String, String>> globals = e.getBindingResult().getGlobalErrors().stream()
                .map(g -> Map.of("field", g.getObjectName(),
                        "message", g.getDefaultMessage() == null ? "invalid" : g.getDefaultMessage()))
                .toList();
        List<Map<String, String>> all = new java.util.ArrayList<>(fields);
        all.addAll(globals);
        return Problems.of(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", "The request has invalid fields",
                Map.of("fieldErrors", all));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail constraintViolation(ConstraintViolationException e) {
        List<Map<String, String>> fields = e.getConstraintViolations().stream()
                .map(v -> Map.of("field", v.getPropertyPath().toString(), "message", v.getMessage())).toList();
        return Problems.of(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", "The request has invalid fields",
                Map.of("fieldErrors", fields));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class, MissingRequestHeaderException.class,
            MissingServletRequestPartException.class, MultipartException.class})
    public ProblemDetail malformed(Exception e) {
        if (e instanceof MaxUploadSizeExceededException) {
            return Problems.of(HttpStatus.PAYLOAD_TOO_LARGE, "FILE_TOO_LARGE", "The file exceeds the 50 MB limit");
        }
        String detail = "The request body could not be read";
        if (e instanceof MissingServletRequestParameterException m) {
            detail = "Missing request parameter: " + m.getParameterName();
        } else if (e instanceof MissingRequestHeaderException m) {
            detail = "Missing request header: " + m.getHeaderName();
        } else if (e instanceof MissingServletRequestPartException m) {
            detail = "Missing request part: " + m.getRequestPartName();
        } else if (e instanceof MethodArgumentTypeMismatchException m) {
            detail = "Invalid value for parameter: " + m.getName();
        }
        return Problems.of(HttpStatus.BAD_REQUEST, "MALFORMED_REQUEST", detail);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ProblemDetail methodNotAllowed(HttpRequestMethodNotSupportedException e) {
        return Problems.of(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED", "Method not allowed");
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ProblemDetail mediaType(HttpMediaTypeNotSupportedException e) {
        return Problems.of(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "UNSUPPORTED_MEDIA_TYPE", "Unsupported content type");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ProblemDetail noResource(NoResourceFoundException e) {
        return Problems.of(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", "Not found");
    }

    @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
    public ProblemDetail accessDenied(Exception e) {
        return Problems.of(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "You do not have permission for this action");
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ProblemDetail optimisticLock(OptimisticLockingFailureException e) {
        return Problems.of(HttpStatus.CONFLICT, "CONCURRENT_MODIFICATION",
                "The record was changed by someone else. Reload and try again.");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail integrity(DataIntegrityViolationException e) {
        String reference = UUID.randomUUID().toString();
        log.warn("Data integrity violation [{}]: {}", reference, e.getMostSpecificCause().getClass().getSimpleName());
        return Problems.of(HttpStatus.CONFLICT, "DUPLICATE_RECORD",
                "The request conflicts with existing data", Map.of("errorReference", reference));
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail unexpected(Exception e) {
        String reference = UUID.randomUUID().toString();
        log.error("Unhandled error [{}]", reference, e);
        return Problems.of(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
                "An unexpected error occurred. Quote the error reference when contacting support.",
                Map.of("errorReference", reference));
    }

    private static Map<String, String> fieldError(FieldError f) {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("field", f.getField());
        m.put("message", f.getDefaultMessage() == null ? "invalid" : f.getDefaultMessage());
        return m;
    }

    /** Legacy exceptions default their code to the class name; never leak that. */
    private static String safeCode(String code) {
        if (code == null || !code.matches("[A-Z0-9_]+")) {
            return "BUSINESS_RULE_VIOLATION";
        }
        return code;
    }
}

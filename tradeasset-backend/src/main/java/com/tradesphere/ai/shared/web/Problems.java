package com.tradesphere.ai.shared.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;

import java.io.IOException;
import java.net.URI;
import java.util.Locale;
import java.util.Map;

/**
 * Builds RFC 7807 problem details. Every error that leaves the API carries a stable
 * machine-readable {@code code} property, which is a versioned contract with the frontend.
 */
public final class Problems {

    public static final MediaType PROBLEM_JSON = MediaType.APPLICATION_PROBLEM_JSON;

    private Problems() {
    }

    public static ProblemDetail of(HttpStatus status, String code, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setType(URI.create("urn:tradeasset:problem:" + code.toLowerCase(Locale.ROOT).replace('_', '-')));
        problem.setTitle(status.getReasonPhrase());
        problem.setProperty("code", code);
        return problem;
    }

    public static ProblemDetail of(HttpStatus status, String code, String detail, Map<String, Object> properties) {
        ProblemDetail problem = of(status, code, detail);
        if (properties != null) {
            properties.forEach(problem::setProperty);
        }
        return problem;
    }

    /** Writes a problem directly to the servlet response, for use in filters and security handlers. */
    public static void write(HttpServletResponse response, ObjectMapper mapper, ProblemDetail problem)
            throws IOException {
        response.setStatus(problem.getStatus());
        response.setContentType(PROBLEM_JSON.toString());
        response.setCharacterEncoding("UTF-8");
        mapper.writeValue(response.getOutputStream(), problem);
    }
}

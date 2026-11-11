package com.tradesphere.ai.shared.web.idempotency;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradesphere.ai.shared.security.AuthenticatedUser;
import com.tradesphere.ai.shared.security.CurrentUser;
import com.tradesphere.ai.shared.web.Problems;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Implements the {@code Idempotency-Key} contract for every mutating endpoint (REQ-REL-001).
 *
 * <ul>
 *   <li>First request with a key: processed, and its response stored.</li>
 *   <li>Repeat with the same key and an identical request: the stored response is replayed with
 *       header {@code Idempotent-Replayed: true}; no new work is done.</li>
 *   <li>Repeat with the same key and a different request: 409 IDEMPOTENCY_KEY_REUSED.</li>
 *   <li>Repeat while the first is still running: 409 IDEMPOTENCY_REQUEST_IN_PROGRESS.</li>
 *   <li>A server error releases the key so the client can retry.</li>
 * </ul>
 *
 * <p>Keys are scoped to the authenticated user and tenant. Authentication endpoints are excluded
 * because their responses carry tokens, which must not be stored.</p>
 */
public class IdempotencyFilter extends OncePerRequestFilter {

    public static final String HEADER = "Idempotency-Key";
    public static final String REPLAYED_HEADER = "Idempotent-Replayed";

    private static final Set<String> MUTATING = Set.of("POST", "PUT", "PATCH", "DELETE");
    private static final Pattern KEY_FORMAT = Pattern.compile("[A-Za-z0-9_\\-:.]{8,100}");

    private final IdempotencyStore store;
    private final ObjectMapper mapper;

    public IdempotencyFilter(IdempotencyStore store, ObjectMapper mapper) {
        this.store = store;
        this.mapper = mapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        return !MUTATING.contains(request.getMethod())
                || request.getHeader(HEADER) == null
                || !path.startsWith("/api/")
                || path.startsWith("/api/v1/auth/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        Optional<AuthenticatedUser> principal = CurrentUser.get();
        if (principal.isEmpty()) {
            chain.doFilter(request, response);
            return;
        }
        AuthenticatedUser user = principal.get();
        String key = request.getHeader(HEADER).trim();
        if (!KEY_FORMAT.matcher(key).matches()) {
            Problems.write(response, mapper, Problems.of(HttpStatus.BAD_REQUEST, "IDEMPOTENCY_KEY_INVALID",
                    "Idempotency-Key must be 8 to 100 characters of letters, digits, '-', '_', ':' or '.'"));
            return;
        }

        String path = request.getRequestURI().substring(request.getContextPath().length());
        HttpServletRequest effectiveRequest = request;
        String hash;
        if (isMultipart(request)) {
            hash = hashMultipart(request, path);
        } else {
            CachedBodyRequest cached = new CachedBodyRequest(request);
            effectiveRequest = cached;
            hash = hash(request.getMethod(), path, request.getQueryString(), List.of(cached.body));
        }

        if (!store.claim(user.tenantId(), user.userId(), key, request.getMethod(), path, hash)) {
            replayOrReject(response, user, key, hash);
            return;
        }

        ContentCachingResponseWrapper wrapped = new ContentCachingResponseWrapper(response);
        boolean stored = false;
        try {
            chain.doFilter(effectiveRequest, wrapped);
            if (wrapped.getStatus() < 500) {
                store.complete(user.tenantId(), user.userId(), key, wrapped.getStatus(), wrapped.getContentType(),
                        wrapped.getContentAsByteArray());
                stored = true;
            }
        } finally {
            if (!stored) {
                store.release(user.tenantId(), user.userId(), key);
            }
            wrapped.copyBodyToResponse();
        }
    }

    private void replayOrReject(HttpServletResponse response, AuthenticatedUser user, String key, String hash)
            throws IOException {
        Optional<IdempotencyStore.StoredRequest> existing = store.find(user.tenantId(), user.userId(), key);
        if (existing.isEmpty()) {
            // Claimed and released between our insert and read; ask the client to retry.
            Problems.write(response, mapper, Problems.of(HttpStatus.CONFLICT, "IDEMPOTENCY_REQUEST_IN_PROGRESS",
                    "A request with this Idempotency-Key is being processed. Retry shortly."));
            return;
        }
        IdempotencyStore.StoredRequest stored = existing.get();
        if (!stored.hash().equals(hash)) {
            Problems.write(response, mapper, Problems.of(HttpStatus.CONFLICT, "IDEMPOTENCY_KEY_REUSED",
                    "This Idempotency-Key was already used for a different request"));
            return;
        }
        if (!stored.completed()) {
            Problems.write(response, mapper, Problems.of(HttpStatus.CONFLICT, "IDEMPOTENCY_REQUEST_IN_PROGRESS",
                    "A request with this Idempotency-Key is being processed. Retry shortly."));
            return;
        }
        response.setStatus(stored.responseStatus());
        response.setHeader(REPLAYED_HEADER, "true");
        if (stored.contentType() != null) {
            response.setContentType(stored.contentType());
        }
        if (stored.body() != null && stored.body().length > 0) {
            response.getOutputStream().write(stored.body());
        }
    }

    private static boolean isMultipart(HttpServletRequest request) {
        String type = request.getContentType();
        return type != null && type.toLowerCase().startsWith("multipart/");
    }

    /** Hashes the parts rather than the raw body: browsers pick a new boundary on every submit. */
    private static String hashMultipart(HttpServletRequest request, String path) throws IOException, ServletException {
        List<Part> parts = new ArrayList<>(request.getParts());
        parts.sort(Comparator.comparing(Part::getName));
        MessageDigest digest = sha256();
        update(digest, request.getMethod());
        update(digest, path);
        update(digest, request.getQueryString());
        byte[] buffer = new byte[8192];
        for (Part part : parts) {
            update(digest, part.getName());
            update(digest, part.getSubmittedFileName());
            try (InputStream in = part.getInputStream()) {
                int read;
                while ((read = in.read(buffer)) != -1) {
                    digest.update(buffer, 0, read);
                }
            }
        }
        return HexFormat.of().formatHex(digest.digest());
    }

    private static String hash(String method, String path, String query, List<byte[]> bodies) {
        MessageDigest digest = sha256();
        update(digest, method);
        update(digest, path);
        update(digest, query);
        for (byte[] body : bodies) {
            digest.update(body);
        }
        return HexFormat.of().formatHex(digest.digest());
    }

    private static void update(MessageDigest digest, String value) {
        digest.update((value == null ? "" : value).getBytes(StandardCharsets.UTF_8));
        digest.update((byte) 0);
    }

    private static MessageDigest sha256() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    /** Reads the body once so it can be hashed and then read again by the controller. */
    private static final class CachedBodyRequest extends HttpServletRequestWrapper {
        private final byte[] body;

        CachedBodyRequest(HttpServletRequest request) throws IOException {
            super(request);
            this.body = request.getInputStream().readAllBytes();
        }

        @Override
        public ServletInputStream getInputStream() {
            ByteArrayInputStream in = new ByteArrayInputStream(body);
            return new ServletInputStream() {
                @Override
                public boolean isFinished() {
                    return in.available() == 0;
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setReadListener(ReadListener listener) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public int read() {
                    return in.read();
                }

                @Override
                public int read(byte[] b, int off, int len) {
                    return in.read(b, off, len);
                }
            };
        }

        @Override
        public BufferedReader getReader() {
            String encoding = getCharacterEncoding() == null ? "UTF-8" : getCharacterEncoding();
            return new BufferedReader(new InputStreamReader(getInputStream(), java.nio.charset.Charset.forName(encoding)));
        }
    }
}

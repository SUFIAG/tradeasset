package com.tradesphere.ai.shared.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Issues and verifies signed tokens (HS256). Access tokens live 15 minutes (REQ-SEC-003). Purpose
 * tokens are short-lived, single-step tokens such as the MFA challenge token; they are never
 * accepted as access tokens.
 *
 * <p>Refresh tokens are not JWTs: they are opaque random values stored hashed by the identity
 * module so that rotation and revocation are immediate (REQ-SEC-003, REQ-SEC-004).</p>
 */
@Component
public class JwtCodec {

    public static final String TYPE_ACCESS = "access";

    private static final String CLAIM_TYPE = "typ";
    private static final String CLAIM_TENANT = "tid";
    private static final String CLAIM_USERNAME = "usr";
    private static final String CLAIM_NAME = "name";
    private static final String CLAIM_ROLES = "roles";
    private static final String CLAIM_PERMISSIONS = "perms";
    private static final String CLAIM_CUSTOMER = "cid";

    private final SecretKey key;
    private final Duration accessTokenTtl;
    private final String issuer;
    private final Clock clock;

    public JwtCodec(@Value("${tradesphere.security.jwt.secret:}") String secret,
                    @Value("${tradesphere.security.jwt.access-token-ttl:PT15M}") Duration accessTokenTtl,
                    @Value("${tradesphere.security.jwt.issuer:tradeasset}") String issuer,
                    Clock clock) {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException(
                    "tradesphere.security.jwt.secret (JWT_SECRET) must be set and at least 32 bytes long");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenTtl = accessTokenTtl;
        this.issuer = issuer;
        this.clock = clock;
    }

    public Duration accessTokenTtl() {
        return accessTokenTtl;
    }

    public IssuedToken issueAccessToken(AuthenticatedUser user) {
        Instant now = clock.instant();
        Instant expiresAt = now.plus(accessTokenTtl);
        var builder = Jwts.builder()
                .id(UUID.randomUUID().toString())
                .issuer(issuer)
                .subject(user.userId().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .claim(CLAIM_TYPE, TYPE_ACCESS)
                .claim(CLAIM_TENANT, user.tenantId().toString())
                .claim(CLAIM_USERNAME, user.username())
                .claim(CLAIM_NAME, user.displayName())
                .claim(CLAIM_ROLES, user.roles())
                .claim(CLAIM_PERMISSIONS, user.permissions());
        if (user.customerId() != null) {
            builder.claim(CLAIM_CUSTOMER, user.customerId().toString());
        }
        return new IssuedToken(builder.signWith(key).compact(), expiresAt);
    }

    /**
     * Issues a token usable only for one step of a flow, identified by {@code purpose}.
     * Extra claims must not contain secrets; the token is signed, not encrypted.
     */
    public IssuedToken issuePurposeToken(String purpose, UUID userId, UUID tenantId, Duration ttl,
                                         Map<String, Object> extraClaims) {
        if (TYPE_ACCESS.equals(purpose)) {
            throw new IllegalArgumentException("Use issueAccessToken for access tokens");
        }
        Instant now = clock.instant();
        Instant expiresAt = now.plus(ttl);
        var builder = Jwts.builder()
                .id(UUID.randomUUID().toString())
                .issuer(issuer)
                .subject(userId.toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .claim(CLAIM_TYPE, purpose)
                .claim(CLAIM_TENANT, tenantId.toString());
        if (extraClaims != null) {
            extraClaims.forEach(builder::claim);
        }
        return new IssuedToken(builder.signWith(key).compact(), expiresAt);
    }

    /** Verifies an access token. Returns a typed outcome so the caller can report expiry distinctly. */
    public Verification verifyAccessToken(String token) {
        try {
            Claims claims = parse(token);
            if (!TYPE_ACCESS.equals(claims.get(CLAIM_TYPE, String.class))) {
                return Verification.invalid();
            }
            String customer = claims.get(CLAIM_CUSTOMER, String.class);
            AuthenticatedUser user = new AuthenticatedUser(
                    UUID.fromString(claims.getSubject()),
                    UUID.fromString(claims.get(CLAIM_TENANT, String.class)),
                    claims.get(CLAIM_USERNAME, String.class),
                    claims.get(CLAIM_NAME, String.class),
                    stringSet(claims.get(CLAIM_ROLES)),
                    stringSet(claims.get(CLAIM_PERMISSIONS)),
                    customer == null ? null : UUID.fromString(customer));
            return new Verification(Optional.of(user), false);
        } catch (ExpiredJwtException e) {
            return new Verification(Optional.empty(), true);
        } catch (JwtException | IllegalArgumentException e) {
            return Verification.invalid();
        }
    }

    /** Verifies a purpose token and returns its claims, or empty when invalid, expired or of another purpose. */
    public Optional<PurposeClaims> verifyPurposeToken(String token, String purpose) {
        try {
            Claims claims = parse(token);
            if (!purpose.equals(claims.get(CLAIM_TYPE, String.class))) {
                return Optional.empty();
            }
            Map<String, Object> extra = new HashMap<>(claims);
            return Optional.of(new PurposeClaims(UUID.fromString(claims.getSubject()),
                    UUID.fromString(claims.get(CLAIM_TENANT, String.class)), claims.getId(),
                    claims.getExpiration().toInstant(), extra));
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .requireIssuer(issuer)
                .clock(() -> Date.from(clock.instant()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private static Set<String> stringSet(Object raw) {
        Set<String> result = new HashSet<>();
        if (raw instanceof Collection<?> values) {
            for (Object v : values) {
                if (v != null) {
                    result.add(v.toString());
                }
            }
        }
        return result;
    }

    public record IssuedToken(String token, Instant expiresAt) {
    }

    public record Verification(Optional<AuthenticatedUser> user, boolean expired) {
        static Verification invalid() {
            return new Verification(Optional.empty(), false);
        }
    }

    /**
     * @param tokenId the JWT ID; use it to make a purpose token single-use
     */
    public record PurposeClaims(UUID userId, UUID tenantId, String tokenId, Instant expiresAt,
                                Map<String, Object> claims) {
    }
}

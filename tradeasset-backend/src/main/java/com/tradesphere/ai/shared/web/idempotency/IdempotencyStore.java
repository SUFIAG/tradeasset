package com.tradesphere.ai.shared.web.idempotency;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static com.tradesphere.ai.shared.adapter.out.persistence.JdbcRows.instant;
import static com.tradesphere.ai.shared.adapter.out.persistence.JdbcRows.ts;

/**
 * Stores idempotency keys and the response of the first request (REQ-REL-001). Runs outside any
 * business transaction: the claim row is committed before the request is processed so that a
 * concurrent duplicate sees it.
 */
@Component
public class IdempotencyStore {

    /** How long a key is remembered. */
    static final Duration RETENTION = Duration.ofHours(24);
    /** An in-progress claim older than this is assumed abandoned (process crash) and may be retaken. */
    static final Duration STALE_CLAIM = Duration.ofMinutes(10);

    private final NamedParameterJdbcTemplate jdbc;
    private final Clock clock;

    public IdempotencyStore(NamedParameterJdbcTemplate jdbc, Clock clock) {
        this.jdbc = jdbc;
        this.clock = clock;
    }

    /** Tries to claim the key. True when this request owns it and must be processed. */
    public boolean claim(UUID tenantId, UUID userId, String key, String method, String path, String hash) {
        Instant now = clock.instant();
        jdbc.update("""
                DELETE FROM idempotency_records
                WHERE tenant_id = :tenantId AND user_id = :userId AND idempotency_key = :key
                  AND ((status = 'IN_PROGRESS' AND created_at < :staleBefore) OR expires_at < :now)
                """, base(tenantId, userId, key)
                .addValue("staleBefore", ts(now.minus(STALE_CLAIM)))
                .addValue("now", ts(now)));
        int inserted = jdbc.update("""
                INSERT INTO idempotency_records (tenant_id, user_id, idempotency_key, request_method, request_path,
                    request_hash, status, created_at, expires_at)
                VALUES (:tenantId, :userId, :key, :method, :path, :hash, 'IN_PROGRESS', :now, :expires)
                ON CONFLICT (tenant_id, user_id, idempotency_key) DO NOTHING
                """, base(tenantId, userId, key)
                .addValue("method", method)
                .addValue("path", path)
                .addValue("hash", hash)
                .addValue("now", ts(now))
                .addValue("expires", ts(now.plus(RETENTION))));
        return inserted == 1;
    }

    public Optional<StoredRequest> find(UUID tenantId, UUID userId, String key) {
        return jdbc.query("""
                SELECT * FROM idempotency_records
                WHERE tenant_id = :tenantId AND user_id = :userId AND idempotency_key = :key
                """, base(tenantId, userId, key), (rs, n) -> new StoredRequest(
                rs.getString("request_method"), rs.getString("request_path"), rs.getString("request_hash"),
                rs.getString("status"), (Integer) rs.getObject("response_status"),
                rs.getString("response_content_type"), rs.getBytes("response_body"),
                instant(rs, "created_at"))).stream().findFirst();
    }

    public void complete(UUID tenantId, UUID userId, String key, int status, String contentType, byte[] body) {
        jdbc.update("""
                UPDATE idempotency_records SET status = 'COMPLETED', response_status = :status,
                    response_content_type = :contentType, response_body = :body, completed_at = :now
                WHERE tenant_id = :tenantId AND user_id = :userId AND idempotency_key = :key
                """, base(tenantId, userId, key)
                .addValue("status", status)
                .addValue("contentType", contentType)
                .addValue("body", body)
                .addValue("now", ts(clock.instant())));
    }

    /** Releases a claim so that the client may retry, used when processing failed with a server error. */
    public void release(UUID tenantId, UUID userId, String key) {
        jdbc.update("""
                DELETE FROM idempotency_records
                WHERE tenant_id = :tenantId AND user_id = :userId AND idempotency_key = :key AND status = 'IN_PROGRESS'
                """, base(tenantId, userId, key));
    }

    @Scheduled(fixedDelayString = "PT1H", initialDelayString = "PT5M")
    public void purgeExpired() {
        jdbc.update("DELETE FROM idempotency_records WHERE expires_at < :now",
                new MapSqlParameterSource("now", ts(clock.instant())));
    }

    private static MapSqlParameterSource base(UUID tenantId, UUID userId, String key) {
        return new MapSqlParameterSource()
                .addValue("tenantId", tenantId)
                .addValue("userId", userId)
                .addValue("key", key);
    }

    public record StoredRequest(String method, String path, String hash, String status, Integer responseStatus,
                                String contentType, byte[] body, Instant createdAt) {
        boolean completed() {
            return "COMPLETED".equals(status);
        }
    }
}

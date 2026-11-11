package com.tradesphere.ai.shared.adapter.out.persistence;

import com.tradesphere.ai.shared.application.port.out.TimelinePort;
import com.tradesphere.ai.shared.domain.timeline.TimelineEntry;
import com.tradesphere.ai.shared.query.PageQuery;
import com.tradesphere.ai.shared.query.PageResult;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.tradesphere.ai.shared.adapter.out.persistence.JdbcRows.*;

@Component
public class JdbcTimelineAdapter implements TimelinePort {

    private static final Map<String, String> SORTS = Map.of("occurredAt", "occurred_at");

    private static final RowMapper<TimelineEntry> ROW = (rs, n) -> new TimelineEntry(
            uuid(rs, "id"), uuid(rs, "tenant_id"), rs.getString("instrument_type"), uuid(rs, "instrument_id"),
            rs.getString("action"), rs.getString("from_state"), rs.getString("to_state"), uuid(rs, "actor_id"),
            rs.getString("actor_name"), rs.getString("comment"), jsonMap(rs, "details"),
            rs.getString("source_address"), instant(rs, "occurred_at"));

    private final NamedParameterJdbcTemplate jdbc;

    public JdbcTimelineAdapter(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void append(TimelineEntry e) {
        jdbc.update("""
                INSERT INTO trade_events (id, tenant_id, instrument_type, instrument_id, action, from_state, to_state,
                    actor_id, actor_name, comment, details, source_address, occurred_at)
                VALUES (:id, :tenantId, :type, :entityId, :action, :fromState, :toState, :actorId, :actorName,
                    :comment, CAST(:details AS jsonb), :source, :at)
                """, new MapSqlParameterSource()
                .addValue("id", e.id())
                .addValue("tenantId", e.tenantId())
                .addValue("type", e.entityType())
                .addValue("entityId", e.entityId())
                .addValue("action", e.action())
                .addValue("fromState", e.fromState())
                .addValue("toState", e.toState())
                .addValue("actorId", e.actorId())
                .addValue("actorName", e.actorName())
                .addValue("comment", e.comment())
                .addValue("details", e.details().isEmpty() ? null : toJson(e.details()))
                .addValue("source", e.sourceAddress())
                .addValue("at", ts(e.occurredAt())));
    }

    @Override
    public List<TimelineEntry> listFor(UUID tenantId, String entityType, UUID entityId) {
        return jdbc.query("""
                SELECT * FROM trade_events
                WHERE tenant_id = :tenantId AND instrument_type = :type AND instrument_id = :entityId
                ORDER BY occurred_at, id
                """, new MapSqlParameterSource()
                .addValue("tenantId", tenantId)
                .addValue("type", entityType)
                .addValue("entityId", entityId), ROW);
    }

    @Override
    public PageResult<TimelineEntry> search(UUID tenantId, AuditFilter f, PageQuery page) {
        StringBuilder where = new StringBuilder(" WHERE tenant_id = :tenantId");
        MapSqlParameterSource p = new MapSqlParameterSource("tenantId", tenantId);
        if (f.entityType() != null) {
            where.append(" AND instrument_type = :type");
            p.addValue("type", f.entityType());
        }
        if (f.entityId() != null) {
            where.append(" AND instrument_id = :entityId");
            p.addValue("entityId", f.entityId());
        }
        if (f.actorId() != null) {
            where.append(" AND actor_id = :actorId");
            p.addValue("actorId", f.actorId());
        }
        if (f.action() != null) {
            where.append(" AND action = :action");
            p.addValue("action", f.action());
        }
        if (f.from() != null) {
            where.append(" AND occurred_at >= :from");
            p.addValue("from", ts(f.from()));
        }
        if (f.to() != null) {
            where.append(" AND occurred_at < :to");
            p.addValue("to", ts(f.to()));
        }
        Long total = jdbc.queryForObject("SELECT COUNT(*) FROM trade_events" + where, p, Long.class);
        p.addValue("limit", page.size()).addValue("offset", page.offset());
        List<TimelineEntry> items = jdbc.query("SELECT * FROM trade_events" + where
                + " ORDER BY " + page.orderBy(SORTS) + ", id LIMIT :limit OFFSET :offset", p, ROW);
        return PageResult.of(items, page, total == null ? 0 : total);
    }
}

package com.tradesphere.ai.shared.adapter.out.persistence;

import com.tradesphere.ai.shared.application.port.out.ApprovalPort;
import com.tradesphere.ai.shared.domain.approval.ApprovalRecord;
import com.tradesphere.ai.shared.domain.approval.ApprovalStatus;
import com.tradesphere.ai.shared.query.PageQuery;
import com.tradesphere.ai.shared.query.PageResult;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.tradesphere.ai.shared.adapter.out.persistence.JdbcRows.*;

@Component
public class JdbcApprovalAdapter implements ApprovalPort {

    private static final Map<String, String> SORTS = Map.of("makerAt", "maker_at", "amount", "amount");

    private static final RowMapper<ApprovalRecord> ROW = (rs, n) -> new ApprovalRecord(
            uuid(rs, "id"), uuid(rs, "tenant_id"), rs.getString("instrument_type"), uuid(rs, "instrument_id"),
            rs.getString("action"), rs.getString("reference"), ApprovalStatus.valueOf(rs.getString("status")),
            money(rs, "amount", "currency"), uuid(rs, "maker_id"), rs.getString("maker_name"),
            instant(rs, "maker_at"), rs.getString("maker_comment"), uuid(rs, "checker_id"),
            rs.getString("checker_name"), instant(rs, "checker_at"), rs.getString("checker_comment"));

    private final NamedParameterJdbcTemplate jdbc;

    public JdbcApprovalAdapter(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void insert(ApprovalRecord r) {
        jdbc.update("""
                INSERT INTO trade_approvals (id, tenant_id, instrument_type, instrument_id, action, reference, status,
                    amount, currency, maker_id, maker_name, maker_at, maker_comment)
                VALUES (:id, :tenantId, :type, :entityId, :action, :reference, :status, :amount, :currency,
                    :makerId, :makerName, :makerAt, :makerComment)
                """, new MapSqlParameterSource()
                .addValue("id", r.id())
                .addValue("tenantId", r.tenantId())
                .addValue("type", r.entityType())
                .addValue("entityId", r.entityId())
                .addValue("action", r.action())
                .addValue("reference", r.reference())
                .addValue("status", r.status().name())
                .addValue("amount", r.amount() == null ? null : r.amount().getAmount())
                .addValue("currency", r.amount() == null ? null : r.amount().getCurrencyCode())
                .addValue("makerId", r.makerId())
                .addValue("makerName", r.makerName())
                .addValue("makerAt", ts(r.makerAt()))
                .addValue("makerComment", r.makerComment()));
    }

    @Override
    public boolean updateDecision(ApprovalRecord r) {
        return jdbc.update("""
                UPDATE trade_approvals SET status = :status, checker_id = :checkerId, checker_name = :checkerName,
                    checker_at = :checkerAt, checker_comment = :checkerComment
                WHERE tenant_id = :tenantId AND id = :id AND status = 'PENDING'
                """, new MapSqlParameterSource()
                .addValue("status", r.status().name())
                .addValue("checkerId", r.checkerId())
                .addValue("checkerName", r.checkerName())
                .addValue("checkerAt", ts(r.checkerAt()))
                .addValue("checkerComment", r.checkerComment())
                .addValue("tenantId", r.tenantId())
                .addValue("id", r.id())) == 1;
    }

    @Override
    public Optional<ApprovalRecord> findById(UUID tenantId, UUID approvalId) {
        return jdbc.query("SELECT * FROM trade_approvals WHERE tenant_id = :tenantId AND id = :id",
                new MapSqlParameterSource().addValue("tenantId", tenantId).addValue("id", approvalId), ROW)
                .stream().findFirst();
    }

    @Override
    public Optional<ApprovalRecord> findPending(UUID tenantId, String entityType, UUID entityId, String action) {
        return jdbc.query("""
                SELECT * FROM trade_approvals
                WHERE tenant_id = :tenantId AND instrument_type = :type AND instrument_id = :entityId
                  AND action = :action AND status = 'PENDING'
                ORDER BY maker_at DESC LIMIT 1
                """, new MapSqlParameterSource()
                .addValue("tenantId", tenantId).addValue("type", entityType).addValue("entityId", entityId)
                .addValue("action", action), ROW).stream().findFirst();
    }

    @Override
    public List<ApprovalRecord> listForEntity(UUID tenantId, String entityType, UUID entityId) {
        return jdbc.query("""
                SELECT * FROM trade_approvals
                WHERE tenant_id = :tenantId AND instrument_type = :type AND instrument_id = :entityId
                ORDER BY maker_at DESC
                """, new MapSqlParameterSource()
                .addValue("tenantId", tenantId).addValue("type", entityType).addValue("entityId", entityId), ROW);
    }

    @Override
    public PageResult<ApprovalRecord> search(UUID tenantId, ApprovalStatus status, String entityType, String action,
                                             PageQuery page) {
        StringBuilder where = new StringBuilder(" WHERE tenant_id = :tenantId");
        MapSqlParameterSource p = new MapSqlParameterSource("tenantId", tenantId);
        if (status != null) {
            where.append(" AND status = :status");
            p.addValue("status", status.name());
        }
        if (entityType != null) {
            where.append(" AND instrument_type = :type");
            p.addValue("type", entityType);
        }
        if (action != null) {
            where.append(" AND action = :action");
            p.addValue("action", action);
        }
        Long total = jdbc.queryForObject("SELECT COUNT(*) FROM trade_approvals" + where, p, Long.class);
        p.addValue("limit", page.size()).addValue("offset", page.offset());
        List<ApprovalRecord> items = jdbc.query("SELECT * FROM trade_approvals" + where
                + " ORDER BY " + page.orderBy(SORTS) + ", id LIMIT :limit OFFSET :offset", p, ROW);
        return PageResult.of(items, page, total == null ? 0 : total);
    }

    @Override
    public long countPending(UUID tenantId) {
        Long count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM trade_approvals WHERE tenant_id = :tenantId AND status = 'PENDING'",
                new MapSqlParameterSource("tenantId", tenantId), Long.class);
        return count == null ? 0 : count;
    }
}

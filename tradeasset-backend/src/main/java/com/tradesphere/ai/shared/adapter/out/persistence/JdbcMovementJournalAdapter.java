package com.tradesphere.ai.shared.adapter.out.persistence;

import com.tradesphere.ai.shared.application.port.out.MovementJournalPort;
import com.tradesphere.ai.shared.domain.journal.Movement;
import com.tradesphere.ai.shared.domain.valueobject.Money;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.tradesphere.ai.shared.adapter.out.persistence.JdbcRows.*;

@Component
public class JdbcMovementJournalAdapter implements MovementJournalPort {

    private static final RowMapper<Movement> ROW = (rs, n) -> new Movement(
            uuid(rs, "id"), uuid(rs, "tenant_id"), rs.getString("instrument_type"), uuid(rs, "instrument_id"),
            uuid(rs, "customer_id"), rs.getString("account"), rs.getString("movement_type"),
            Money.of(rs.getBigDecimal("amount"), rs.getString("currency").trim()), rs.getString("reason"),
            rs.getString("reference"), uuid(rs, "actor_id"), instant(rs, "occurred_at"),
            uuid(rs, "reverses_movement_id"));

    private final NamedParameterJdbcTemplate jdbc;

    public JdbcMovementJournalAdapter(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void post(Collection<Movement> movements) {
        MapSqlParameterSource[] batch = movements.stream().map(m -> new MapSqlParameterSource()
                .addValue("id", m.id())
                .addValue("tenantId", m.tenantId())
                .addValue("type", m.entityType())
                .addValue("entityId", m.entityId())
                .addValue("customerId", m.customerId())
                .addValue("account", m.account())
                .addValue("movementType", m.movementType())
                .addValue("amount", m.amount().getAmount())
                .addValue("currency", m.amount().getCurrencyCode())
                .addValue("reason", m.reason())
                .addValue("reference", m.reference())
                .addValue("actorId", m.actorId())
                .addValue("at", ts(m.occurredAt()))
                .addValue("reverses", m.reversesMovementId())).toArray(MapSqlParameterSource[]::new);
        jdbc.batchUpdate("""
                INSERT INTO trade_movements (id, tenant_id, instrument_type, instrument_id, customer_id, account,
                    movement_type, amount, currency, reason, reference, actor_id, occurred_at, reverses_movement_id)
                VALUES (:id, :tenantId, :type, :entityId, :customerId, :account, :movementType, :amount, :currency,
                    :reason, :reference, :actorId, :at, :reverses)
                """, batch);
    }

    @Override
    public Optional<Movement> findById(UUID tenantId, UUID movementId) {
        return jdbc.query("SELECT * FROM trade_movements WHERE tenant_id = :tenantId AND id = :id",
                new MapSqlParameterSource().addValue("tenantId", tenantId).addValue("id", movementId), ROW)
                .stream().findFirst();
    }

    @Override
    public List<Movement> listForEntity(UUID tenantId, String entityType, UUID entityId) {
        return jdbc.query("""
                SELECT * FROM trade_movements
                WHERE tenant_id = :tenantId AND instrument_type = :type AND instrument_id = :entityId
                ORDER BY occurred_at, id
                """, new MapSqlParameterSource()
                .addValue("tenantId", tenantId).addValue("type", entityType).addValue("entityId", entityId), ROW);
    }

    @Override
    public BigDecimal balance(UUID tenantId, String entityType, UUID entityId, String account, String currency) {
        BigDecimal value = jdbc.queryForObject("""
                SELECT COALESCE(SUM(amount), 0) FROM trade_movements
                WHERE tenant_id = :tenantId AND instrument_type = :type AND instrument_id = :entityId
                  AND account = :account AND currency = :currency
                """, new MapSqlParameterSource()
                .addValue("tenantId", tenantId).addValue("type", entityType).addValue("entityId", entityId)
                .addValue("account", account).addValue("currency", currency), BigDecimal.class);
        return value == null ? BigDecimal.ZERO : value;
    }

    @Override
    public Map<String, BigDecimal> balancesByCurrency(UUID tenantId, String entityType, UUID entityId, String account) {
        Map<String, BigDecimal> result = new LinkedHashMap<>();
        jdbc.query("""
                SELECT currency, SUM(amount) AS balance FROM trade_movements
                WHERE tenant_id = :tenantId AND instrument_type = :type AND instrument_id = :entityId
                  AND account = :account
                GROUP BY currency ORDER BY currency
                """, new MapSqlParameterSource()
                .addValue("tenantId", tenantId).addValue("type", entityType).addValue("entityId", entityId)
                .addValue("account", account), rs -> {
            result.put(rs.getString("currency").trim(), rs.getBigDecimal("balance"));
        });
        return result;
    }

    @Override
    public Map<String, BigDecimal> customerBalances(UUID tenantId, UUID customerId, String account) {
        Map<String, BigDecimal> result = new LinkedHashMap<>();
        jdbc.query("""
                SELECT currency, SUM(amount) AS balance FROM trade_movements
                WHERE tenant_id = :tenantId AND customer_id = :customerId AND account = :account
                GROUP BY currency ORDER BY currency
                """, new MapSqlParameterSource()
                .addValue("tenantId", tenantId).addValue("customerId", customerId).addValue("account", account),
                rs -> {
                    result.put(rs.getString("currency").trim(), rs.getBigDecimal("balance"));
                });
        return result;
    }

    @Override
    public Map<UUID, Map<String, BigDecimal>> customerBalances(UUID tenantId, Collection<UUID> customerIds,
                                                               String account) {
        Map<UUID, Map<String, BigDecimal>> result = new HashMap<>();
        if (customerIds.isEmpty()) {
            return result;
        }
        jdbc.query("""
                SELECT customer_id, currency, SUM(amount) AS balance FROM trade_movements
                WHERE tenant_id = :tenantId AND customer_id IN (:customerIds) AND account = :account
                GROUP BY customer_id, currency
                """, new MapSqlParameterSource()
                .addValue("tenantId", tenantId).addValue("customerIds", customerIds).addValue("account", account),
                rs -> {
                    result.computeIfAbsent(uuid(rs, "customer_id"), k -> new LinkedHashMap<>())
                            .put(rs.getString("currency").trim(), rs.getBigDecimal("balance"));
                });
        return result;
    }

    @Override
    public Map<String, BigDecimal> tenantBalances(UUID tenantId, String account, String entityTypeOrNull) {
        Map<String, BigDecimal> result = new LinkedHashMap<>();
        MapSqlParameterSource p = new MapSqlParameterSource()
                .addValue("tenantId", tenantId).addValue("account", account);
        String typeFilter = "";
        if (entityTypeOrNull != null) {
            typeFilter = " AND instrument_type = :type";
            p.addValue("type", entityTypeOrNull);
        }
        jdbc.query("SELECT currency, SUM(amount) AS balance FROM trade_movements"
                + " WHERE tenant_id = :tenantId AND account = :account" + typeFilter
                + " GROUP BY currency ORDER BY currency", p, rs -> {
            result.put(rs.getString("currency").trim(), rs.getBigDecimal("balance"));
        });
        return result;
    }
}

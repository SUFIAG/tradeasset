package com.tradesphere.ai.shared.adapter.in.rest;

import com.tradesphere.ai.shared.domain.approval.ApprovalRecord;
import com.tradesphere.ai.shared.web.MoneyView;

import java.time.Instant;
import java.util.UUID;

/** A maker-checker record as returned by the API (REQ-MKR-003). */
public record ApprovalView(UUID id, String entityType, UUID entityId, String action, String reference, String status,
                           MoneyView amount, UUID makerId, String makerName, Instant makerAt, String makerComment,
                           UUID checkerId, String checkerName, Instant checkerAt, String checkerComment) {

    public static ApprovalView of(ApprovalRecord r) {
        return new ApprovalView(r.id(), r.entityType(), r.entityId(), r.action(), r.reference(), r.status().name(),
                MoneyView.of(r.amount()), r.makerId(), r.makerName(), r.makerAt(), r.makerComment(), r.checkerId(),
                r.checkerName(), r.checkerAt(), r.checkerComment());
    }
}

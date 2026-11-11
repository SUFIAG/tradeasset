package com.tradesphere.ai.shared.application.port.out;

import com.tradesphere.ai.shared.domain.approval.ApprovalRecord;
import com.tradesphere.ai.shared.domain.approval.ApprovalStatus;
import com.tradesphere.ai.shared.query.PageQuery;
import com.tradesphere.ai.shared.query.PageResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Maker-checker register (table {@code trade_approvals}). */
public interface ApprovalPort {

    void insert(ApprovalRecord record);

    /**
     * Writes the decision only if the stored row is still PENDING.
     *
     * @return false when another checker decided first
     */
    boolean updateDecision(ApprovalRecord decided);

    Optional<ApprovalRecord> findById(UUID tenantId, UUID approvalId);

    Optional<ApprovalRecord> findPending(UUID tenantId, String entityType, UUID entityId, String action);

    /** Newest first. */
    List<ApprovalRecord> listForEntity(UUID tenantId, String entityType, UUID entityId);

    /** Sort properties: makerAt, amount. Every filter is optional. */
    PageResult<ApprovalRecord> search(UUID tenantId, ApprovalStatus status, String entityType, String action,
                                      PageQuery page);

    long countPending(UUID tenantId);
}

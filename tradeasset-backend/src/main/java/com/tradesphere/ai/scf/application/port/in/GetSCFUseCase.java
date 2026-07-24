package com.tradesphere.ai.scf.application.port.in;

import com.tradesphere.ai.scf.application.port.in.dto.SCFResponse;
import com.tradesphere.ai.scf.domain.model.SCFStatus;
import com.tradesphere.ai.scf.domain.model.SCFType;

import java.util.List;
import java.util.UUID;

/**
 * Use case for querying SCF transactions
 */
public interface GetSCFUseCase {

    SCFResponse getSCFById(UUID id, String tenantId);

    SCFResponse getSCFByReference(String reference, String tenantId);

    List<SCFResponse> getAllSCF(String tenantId);

    List<SCFResponse> getSCFByStatus(String tenantId, SCFStatus status);

    List<SCFResponse> getSCFByType(String tenantId, SCFType type);

    List<SCFResponse> getSCFBySupplier(UUID supplierId, String tenantId);

    List<SCFResponse> getSCFByBuyer(UUID buyerId, String tenantId);

    List<SCFResponse> getSCFByFinancier(UUID financierId, String tenantId);

    List<SCFResponse> getSCFDueSoon(String tenantId, int days);

    List<SCFResponse> getSCFOverdue(String tenantId);
}


package com.tradesphere.ai.scf.application.port.out;

import com.tradesphere.ai.scf.domain.model.SCFStatus;
import com.tradesphere.ai.scf.domain.model.SCFType;
import com.tradesphere.ai.scf.domain.model.SupplyChainFinance;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Port for Supply Chain Finance
 */
public interface SCFRepository {

    SupplyChainFinance save(SupplyChainFinance scf);

    Optional<SupplyChainFinance> findById(UUID id, String tenantId);

    Optional<SupplyChainFinance> findByReference(String scfReference, String tenantId);

    Optional<SupplyChainFinance> findByInvoiceNumber(String invoiceNumber, String tenantId);

    List<SupplyChainFinance> findByTenantId(String tenantId);

    List<SupplyChainFinance> findByStatus(String tenantId, SCFStatus status);

    List<SupplyChainFinance> findByType(String tenantId, SCFType type);

    List<SupplyChainFinance> findBySupplier(UUID supplierId, String tenantId);

    List<SupplyChainFinance> findByBuyer(UUID buyerId, String tenantId);

    List<SupplyChainFinance> findByFinancier(UUID financierId, String tenantId);

    List<SupplyChainFinance> findDueSoon(String tenantId, int days);

    List<SupplyChainFinance> findOverdue(String tenantId);

    void delete(UUID id);

    boolean exists(UUID id, String tenantId);
}


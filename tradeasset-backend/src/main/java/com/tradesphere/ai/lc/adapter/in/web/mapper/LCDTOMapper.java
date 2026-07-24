package com.tradesphere.ai.lc.adapter.in.web.mapper;

import com.tradesphere.ai.lc.adapter.in.web.dto.CreateLCRequest;
import com.tradesphere.ai.lc.adapter.in.web.dto.LCResponse;
import com.tradesphere.ai.lc.application.port.in.CreateLCCommand;
import com.tradesphere.ai.lc.domain.model.LetterOfCredit;
import com.tradesphere.ai.shared.domain.valueobject.Money;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

/**
 * Mapper between LC REST DTOs and domain objects
 */
@Component
public class LCDTOMapper {
    
    public CreateLCCommand toCommand(CreateLCRequest request, UUID tenantId, UUID createdBy) {
        return CreateLCCommand.builder()
                .tenantId(tenantId)
                .applicantId(request.getApplicantId())
                .applicantName(request.getApplicantName())
                .applicantAddress(request.getApplicantAddress())
                .beneficiaryId(request.getBeneficiaryId())
                .beneficiaryName(request.getBeneficiaryName())
                .beneficiaryAddress(request.getBeneficiaryAddress())
                .lcAmount(Money.of(request.getLcAmount(), request.getCurrency()))
                .lcType(request.getLcType())
                .expiryDate(request.getExpiryDate())
                .lastShipmentDate(request.getLastShipmentDate())
                .incoterm(request.getIncoterm())
                .goodsDescription(request.getGoodsDescription())
                .portOfLoading(request.getPortOfLoading())
                .portOfDischarge(request.getPortOfDischarge())
                .placeOfDestination(request.getPlaceOfDestination())
                .partialShipmentAllowed(request.getPartialShipmentAllowed())
                .transshipmentAllowed(request.getTransshipmentAllowed())
                .hsCode(request.getHsCode())
                .documentsRequired(request.getDocumentsRequired())
                .createdBy(createdBy)
                .build();
    }
    
    public LCResponse toResponse(LetterOfCredit lc) {
        return LCResponse.builder()
                .id(lc.getId().asString())
                .lcNumber(lc.getLcNumber() != null ? lc.getLcNumber().value() : null)
                .lcType(lc.getLcType())
                .status(lc.getStatus())
                .applicantId(lc.getApplicantId().getValue().toString())
                .applicantName(lc.getApplicantName())
                .beneficiaryId(lc.getBeneficiaryId().getValue().toString())
                .beneficiaryName(lc.getBeneficiaryName())
                .lcAmount(lc.getLcAmount().getAmount())
                .currency(lc.getCurrency())
                .availableAmount(lc.getAvailableAmount() != null ? lc.getAvailableAmount().getAmount() : null)
                .applicationDate(lc.getApplicationDate())
                .issueDate(lc.getIssueDate())
                .expiryDate(lc.getExpiryDate())
                .lastShipmentDate(lc.getLastShipmentDate())
                .incoterm(lc.getIncoterm())
                .portOfLoading(lc.getPortOfLoading())
                .portOfDischarge(lc.getPortOfDischarge())
                .goodsDescription(lc.getGoodsDescription())
                .partialShipmentAllowed(lc.isPartialShipmentAllowed())
                .transshipmentAllowed(lc.isTransshipmentAllowed())
                .submittedAt(lc.getSubmittedAt())
                .approvedAt(lc.getApprovedAt())
                .rejectionReason(lc.getRejectionReason())
                .version(lc.getVersion())
                .createdAt(lc.getCreatedAt() != null ? LocalDateTime.ofInstant(lc.getCreatedAt(), ZoneOffset.UTC) : null)
                .updatedAt(lc.getUpdatedAt() != null ? LocalDateTime.ofInstant(lc.getUpdatedAt(), ZoneOffset.UTC) : null)
                .build();
    }
}


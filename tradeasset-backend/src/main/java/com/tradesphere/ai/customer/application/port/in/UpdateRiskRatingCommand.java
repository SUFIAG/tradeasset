package com.tradesphere.ai.customer.application.port.in;

import com.tradesphere.ai.customer.domain.model.RiskRating;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Command to update customer risk rating
 */
@Value
@Builder
public class UpdateRiskRatingCommand {
    String customerId;
    RiskRating riskRating;
    BigDecimal riskScore;
    String reason;
    UUID updatedBy;
}


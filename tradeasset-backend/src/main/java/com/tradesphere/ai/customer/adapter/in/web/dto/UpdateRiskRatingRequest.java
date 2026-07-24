package com.tradesphere.ai.customer.adapter.in.web.dto;

import com.tradesphere.ai.customer.domain.model.RiskRating;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * REST DTO for updating risk rating
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRiskRatingRequest {
    
    @NotNull(message = "Risk rating is required")
    private RiskRating riskRating;
    
    private BigDecimal riskScore;
    
    private String reason;
}


package com.tradesphere.ai.customer.adapter.in.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * REST DTO for setting credit limit
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetCreditLimitRequest {
    
    @NotNull(message = "Amount is required")
    private BigDecimal amount;
    
    @NotNull(message = "Currency is required")
    private String currency;
}


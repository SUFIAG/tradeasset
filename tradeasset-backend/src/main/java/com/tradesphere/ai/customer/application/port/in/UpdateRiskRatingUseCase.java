package com.tradesphere.ai.customer.application.port.in;

import com.tradesphere.ai.customer.domain.model.Customer;

/**
 * Inbound port for updating risk rating
 */
public interface UpdateRiskRatingUseCase {
    Customer updateRiskRating(UpdateRiskRatingCommand command);
}


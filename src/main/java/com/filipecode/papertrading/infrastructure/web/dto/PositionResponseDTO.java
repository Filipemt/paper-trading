package com.filipecode.papertrading.infrastructure.web.dto;

import java.math.BigDecimal;

public record PositionResponseDTO(
        String ticker,
        String companyName,
        int quantity,
        BigDecimal averagePrice,
        BigDecimal currentPrice,
        BigDecimal totalValue
) {
}

package com.filipecode.papertrading.infrastructure.web.dto;

import java.math.BigDecimal;
import java.util.List;

public record PortfolioResponseDTO(
        BigDecimal balance,
        BigDecimal totalAssetsValue,
        BigDecimal totalPortfolioValue,
        List<PositionResponseDTO> positions
) {
}

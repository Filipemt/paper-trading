package com.filipecode.papertrading.infrastructure.web.dto;

import com.filipecode.papertrading.domain.model.asset.AssetType;

import java.math.BigDecimal;

public record AssetResponseDTO(
        String ticker,
        String companyName,
        AssetType type,
        BigDecimal price
) {
}

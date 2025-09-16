package com.filipecode.papertrading.infrastructure.web.dto;

import com.filipecode.papertrading.domain.model.asset.AssetType;

public record AssetResponseDTO(
        Long id,
        String ticker,
        String companyName,
        AssetType type
) {
}

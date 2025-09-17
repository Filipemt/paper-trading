package com.filipecode.papertrading.application.usecase;

import com.filipecode.papertrading.infrastructure.web.dto.AssetResponseDTO;

public interface FindAssetByTickerUseCase {
    AssetResponseDTO findByTicker(String ticker);
}

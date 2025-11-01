package com.filipecode.papertrading.application.port.in;

import com.filipecode.papertrading.infrastructure.web.dto.AssetResponseDTO;

public interface FindAssetByTickerUseCase {
    AssetResponseDTO findByTicker(String ticker);
}

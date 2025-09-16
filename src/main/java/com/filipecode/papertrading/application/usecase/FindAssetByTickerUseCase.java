package com.filipecode.papertrading.application.usecase;

import com.filipecode.papertrading.domain.model.asset.Asset;

public interface FindAssetByTickerUseCase {
    Asset findByTicker(String ticker);
}

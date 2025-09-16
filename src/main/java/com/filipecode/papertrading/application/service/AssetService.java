package com.filipecode.papertrading.application.service;

import com.filipecode.papertrading.application.usecase.FindAssetByTickerUseCase;
import com.filipecode.papertrading.domain.exception.AssetNotFoundException;
import com.filipecode.papertrading.domain.model.asset.Asset;
import com.filipecode.papertrading.domain.repository.AssetRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class AssetService implements FindAssetByTickerUseCase {

    private final AssetRepositoryPort assetRepositoryPort;

    public AssetService(AssetRepositoryPort assetRepositoryPort) {
        this.assetRepositoryPort = assetRepositoryPort;
    }

    @Override
    public Asset findByTicker(String ticker) {
        return assetRepositoryPort.findByTicker(ticker)
                .orElseThrow(() -> new AssetNotFoundException("Ativo não encontrado com o ticker: " + ticker));
    }
}

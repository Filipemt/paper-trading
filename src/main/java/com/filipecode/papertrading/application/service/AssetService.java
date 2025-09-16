package com.filipecode.papertrading.application.service;

import com.filipecode.papertrading.application.usecase.FindAssetByTickerUseCase;
import com.filipecode.papertrading.application.usecase.ListAssetsUseCase;
import com.filipecode.papertrading.domain.exception.AssetNotFoundException;
import com.filipecode.papertrading.domain.model.asset.Asset;
import com.filipecode.papertrading.domain.model.asset.AssetType;
import com.filipecode.papertrading.domain.repository.AssetRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AssetService implements FindAssetByTickerUseCase, ListAssetsUseCase {

    private final AssetRepositoryPort assetRepositoryPort;

    public AssetService(AssetRepositoryPort assetRepositoryPort) {
        this.assetRepositoryPort = assetRepositoryPort;
    }

    @Override
    public Asset findByTicker(String ticker) {
        return assetRepositoryPort.findByTicker(ticker)
                .orElseThrow(() -> new AssetNotFoundException("Ativo não encontrado com o ticker: " + ticker));
    }

    @Override
    public Page<Asset> listAll(AssetType type, Pageable pageable) {
        return assetRepositoryPort.findAll(type, pageable);
    }
}

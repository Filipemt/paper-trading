package com.filipecode.papertrading.application.service;

import com.filipecode.papertrading.application.port.in.FindAssetByTickerUseCase;
import com.filipecode.papertrading.application.port.in.ListAssetsUseCase;
import com.filipecode.papertrading.domain.exception.AssetNotFoundException;
import com.filipecode.papertrading.domain.model.asset.Asset;
import com.filipecode.papertrading.domain.model.asset.AssetType;
import com.filipecode.papertrading.application.port.out.AssetRepositoryPort;
import com.filipecode.papertrading.application.port.out.PriceProviderPort;
import com.filipecode.papertrading.infrastructure.web.dto.AssetResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AssetService implements FindAssetByTickerUseCase, ListAssetsUseCase {

    private final AssetRepositoryPort assetRepositoryPort;
    private final PriceProviderPort priceProvider;

    public AssetService(AssetRepositoryPort assetRepositoryPort, PriceProviderPort priceProvider) {
        this.assetRepositoryPort = assetRepositoryPort;
        this.priceProvider = priceProvider;
    }

    @Override
    public AssetResponseDTO findByTicker(String ticker) {
        Asset asset = assetRepositoryPort.findByTicker(ticker)
                .orElseThrow(() -> new AssetNotFoundException("Ativo não encontrado com o ticker: " + ticker));

        BigDecimal currentPrice = this.priceProvider.getCurrentPrice(asset.getTicker());

        return new AssetResponseDTO(
                asset.getTicker(),
                asset.getCompanyName(),
                asset.getType(),
                currentPrice
        );
    }

    @Override
    public Page<AssetResponseDTO> listAll(AssetType type, Pageable pageable) {
        Page<Asset> assetPage = assetRepositoryPort.findAll(type, pageable);

        return assetPage.map( asset -> {
            BigDecimal currentPrice = this.priceProvider.getCurrentPrice(asset.getTicker());

            return new AssetResponseDTO(
                    asset.getTicker(),
                    asset.getCompanyName(),
                    asset.getType(),
                    currentPrice
            );
        });

    }
}

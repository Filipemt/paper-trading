package com.filipecode.papertrading.infrastructure.web.controller;

import com.filipecode.papertrading.application.usecase.FindAssetByTickerUseCase;
import com.filipecode.papertrading.application.usecase.ListAssetsUseCase;
import com.filipecode.papertrading.domain.model.asset.Asset;
import com.filipecode.papertrading.domain.model.asset.AssetType;
import com.filipecode.papertrading.infrastructure.web.dto.AssetResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/assets")
public class AssetController {

    private final FindAssetByTickerUseCase findAssetByTickerUseCase;
    private final ListAssetsUseCase listAssetsUseCase;

    public AssetController(FindAssetByTickerUseCase findAssetByTickerUseCase, ListAssetsUseCase listAssetsUseCase) {
        this.findAssetByTickerUseCase = findAssetByTickerUseCase;
        this.listAssetsUseCase = listAssetsUseCase;
    }

    @GetMapping("/{ticker}")
    public ResponseEntity<AssetResponseDTO> getAssetByTicker(@PathVariable String ticker) {
        Asset asset = findAssetByTickerUseCase.findByTicker(ticker);
        AssetResponseDTO response = new AssetResponseDTO(
                asset.getId(),
                asset.getTicker(),
                asset.getCompanyName(),
                asset.getType()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<AssetResponseDTO>> listAssets(@RequestParam(required = false) AssetType type,
                                                             Pageable pageable) {

        Page<Asset> assetPage = listAssetsUseCase.listAll(type, pageable);
        Page<AssetResponseDTO> response = assetPage.map(asset -> new AssetResponseDTO(
                asset.getId(),
                asset.getTicker(),
                asset.getCompanyName(),
                asset.getType()
        ));
        return ResponseEntity.ok(response);
    }
}

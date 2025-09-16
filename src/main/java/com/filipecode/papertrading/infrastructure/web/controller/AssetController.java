package com.filipecode.papertrading.infrastructure.web.controller;

import com.filipecode.papertrading.application.usecase.FindAssetByTickerUseCase;
import com.filipecode.papertrading.domain.model.asset.Asset;
import com.filipecode.papertrading.infrastructure.web.dto.AssetResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/assets")
public class AssetController {

    private final FindAssetByTickerUseCase findAssetByTickerUseCase;

    public AssetController(FindAssetByTickerUseCase findAssetByTickerUseCase) {
        this.findAssetByTickerUseCase = findAssetByTickerUseCase;
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
}

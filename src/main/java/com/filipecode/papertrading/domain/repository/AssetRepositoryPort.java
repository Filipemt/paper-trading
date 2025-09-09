package com.filipecode.papertrading.domain.repository;

import com.filipecode.papertrading.domain.model.asset.Asset;
import com.filipecode.papertrading.domain.model.asset.AssetType;

import java.util.List;
import java.util.Optional;

public interface AssetRepositoryPort {
    // Métodos para admin
    Asset save(Asset asset);
    void deleteById(Long id);

    // Métodos de leitura para a aplicação
    Optional<Asset> findByTicker(String ticker);
    Optional<Asset> findById(Long id);
    List<Asset> findAll();
    List<Asset> findByType(AssetType type);
}

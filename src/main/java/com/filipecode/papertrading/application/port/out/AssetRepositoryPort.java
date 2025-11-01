package com.filipecode.papertrading.application.port.out;

import com.filipecode.papertrading.domain.model.asset.Asset;
import com.filipecode.papertrading.domain.model.asset.AssetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AssetRepositoryPort {

    Asset save(Asset asset);

    Optional<Asset> findById(Long id);

    Optional<Asset> findByTicker(String ticker);

    void deleteById(Long id);

    Page<Asset> findAll(AssetType type, Pageable pageable);
}

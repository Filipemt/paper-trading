package com.filipecode.papertrading.infrastructure.adapter.repository.asset;

import com.filipecode.papertrading.domain.model.asset.Asset;
import com.filipecode.papertrading.domain.model.asset.AssetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaAssetRepository extends JpaRepository<Asset, Long> {

    Optional<Asset> findByTicker(String ticker);

    Page<Asset> findByType(AssetType type, Pageable pageable);

}

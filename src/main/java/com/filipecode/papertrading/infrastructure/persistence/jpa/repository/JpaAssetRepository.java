package com.filipecode.papertrading.infrastructure.persistence.jpa.repository;

import com.filipecode.papertrading.domain.model.asset.Asset;
import com.filipecode.papertrading.domain.model.asset.AssetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaAssetRepository extends JpaRepository<Asset, Long> {

    Optional<Asset> findByTicker(String ticker);
    List<Asset> findByType(AssetType type);

}

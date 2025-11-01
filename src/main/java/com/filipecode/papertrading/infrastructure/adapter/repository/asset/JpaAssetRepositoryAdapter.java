package com.filipecode.papertrading.infrastructure.adapter.repository.asset;

import com.filipecode.papertrading.domain.model.asset.Asset;
import com.filipecode.papertrading.domain.model.asset.AssetType;
import com.filipecode.papertrading.application.port.out.AssetRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaAssetRepositoryAdapter implements AssetRepositoryPort {
    private final JpaAssetRepository jpaRepository;

    public JpaAssetRepositoryAdapter(JpaAssetRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Asset save(Asset asset) {
        return jpaRepository.save(asset);
    }

    @Override
    public Optional<Asset> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<Asset> findByTicker(String ticker) {
        return jpaRepository.findByTicker(ticker);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public Page<Asset> findAll(AssetType type, Pageable pageable) {
        if (type != null) {
            return jpaRepository.findByType(type, pageable);
        }
        return jpaRepository.findAll(pageable);
    }
}

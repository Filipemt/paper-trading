package com.filipecode.papertrading.infrastructure.persistence.jpa.repository;

import com.filipecode.papertrading.domain.model.asset.Asset;
import com.filipecode.papertrading.domain.model.asset.AssetType;
import com.filipecode.papertrading.domain.repository.AssetRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaAssetRepositoryAdapter implements AssetRepositoryPort {

    private JpaAssetRepository jpaRepository;

    public JpaAssetRepositoryAdapter(JpaAssetRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Asset save(Asset asset) {
        return jpaRepository.save(asset);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public Optional<Asset> findByTicker(String ticker) {
        return jpaRepository.findByTicker(ticker);
    }

    @Override
    public Optional<Asset> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Asset> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public List<Asset> findByType(AssetType type) {
        return jpaRepository.findByType(type);
    }
}

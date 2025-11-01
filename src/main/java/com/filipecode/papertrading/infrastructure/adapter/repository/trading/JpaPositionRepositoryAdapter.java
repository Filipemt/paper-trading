package com.filipecode.papertrading.infrastructure.adapter.repository.trading;

import com.filipecode.papertrading.domain.model.asset.Asset;
import com.filipecode.papertrading.domain.model.trading.Position;
import com.filipecode.papertrading.domain.model.user.Portfolio;
import com.filipecode.papertrading.application.port.out.PositionRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaPositionRepositoryAdapter implements PositionRepositoryPort {
    private final JpaPositionRepository jpaRepository;

    public JpaPositionRepositoryAdapter(JpaPositionRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Position save(Position position) {
        return jpaRepository.save(position);
    }

    @Override
    public Optional<Position> findByPortfolioAndAsset(Portfolio portfolio, Asset asset) {
        return jpaRepository.findByPortfolioAndAsset(portfolio, asset);
    }
}

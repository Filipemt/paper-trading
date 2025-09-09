package com.filipecode.papertrading.infrastructure.persistence.jpa.repository.trading;

import com.filipecode.papertrading.domain.model.asset.Asset;
import com.filipecode.papertrading.domain.model.trading.Position;
import com.filipecode.papertrading.domain.model.user.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaPositionRepository extends JpaRepository<Position, Long> {
    Optional<Position> findByPortfolioAndAsset(Portfolio portfolio, Asset asset);
}

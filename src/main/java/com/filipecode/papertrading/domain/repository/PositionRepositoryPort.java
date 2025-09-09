package com.filipecode.papertrading.domain.repository;

import com.filipecode.papertrading.domain.model.asset.Asset;
import com.filipecode.papertrading.domain.model.trading.Position;
import com.filipecode.papertrading.domain.model.user.Portfolio;

import java.util.Optional;

public interface PositionRepositoryPort {
    Position save(Position position);

//      O principal method de busca: encontra a posição de um ativo específico dentro de uma carteira específica.
    Optional<Position> findByPortfolioAndAsset(Portfolio portfolio, Asset asset);
}

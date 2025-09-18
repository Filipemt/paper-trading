package com.filipecode.papertrading.application.service;

import com.filipecode.papertrading.application.usecase.ViewPortfolioUseCase;
import com.filipecode.papertrading.domain.model.trading.Position;
import com.filipecode.papertrading.domain.model.user.Portfolio;
import com.filipecode.papertrading.domain.model.user.User;
import com.filipecode.papertrading.domain.repository.PortfolioRepositoryPort;
import com.filipecode.papertrading.domain.repository.PriceProviderPort;
import com.filipecode.papertrading.infrastructure.web.dto.PortfolioResponseDTO;
import com.filipecode.papertrading.infrastructure.web.dto.PositionResponseDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PortfolioService implements ViewPortfolioUseCase {

    private final PortfolioRepositoryPort portfolioRepositoryPort;
    private final PriceProviderPort priceProvider;

    public PortfolioService(PortfolioRepositoryPort portfolioRepositoryPort, PriceProviderPort priceProvider) {
        this.portfolioRepositoryPort = portfolioRepositoryPort;
        this.priceProvider = priceProvider;
    }

    @Override
    public PortfolioResponseDTO view() {

        // Buscar contexto da aplicação para retornar o usuário logado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        // Buscar os dados brutos do banco de dados
        Portfolio portfolio = portfolioRepositoryPort.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Portfólio não encontrado"));

        // Lógica para calcular totalValue

        List<Position> positions = portfolio.getPositions();
        List<PositionResponseDTO> positionsDTOs = positions.stream()
                .map(position -> {
                    BigDecimal currentPrice = priceProvider.getCurrentPrice(position.getAsset().getTicker());
                    BigDecimal totalValue = currentPrice.multiply(BigDecimal.valueOf(position.getQuantity()));

                    PositionResponseDTO positionDTO = new PositionResponseDTO(
                            position.getAsset().getTicker(),
                            position.getAsset().getCompanyName(),
                            position.getQuantity(),
                            position.getAveragePrice(),
                            currentPrice,
                            totalValue
                    );

                    return positionDTO;
                })
                .collect(Collectors.toList());

        BigDecimal totalAssetsValue = positionsDTOs.stream()
                .map(PositionResponseDTO::totalValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPortofolioValue = portfolio.getBalance().add(totalAssetsValue);

        return new PortfolioResponseDTO(
                portfolio.getBalance(),
                totalAssetsValue,
                totalPortofolioValue,
                positionsDTOs
        );
    }
}

package com.filipecode.papertrading.application.service;

import com.filipecode.papertrading.application.usecase.CreateOrderUseCase;
import com.filipecode.papertrading.domain.exception.AssetNotFoundException;
import com.filipecode.papertrading.domain.exception.InsufficientFundsException;
import com.filipecode.papertrading.domain.exception.PortfolioNotFoundException;
import com.filipecode.papertrading.domain.model.asset.Asset;
import com.filipecode.papertrading.domain.model.trading.*;
import com.filipecode.papertrading.domain.model.user.Portfolio;
import com.filipecode.papertrading.domain.model.user.User;
import com.filipecode.papertrading.domain.repository.*;
import com.filipecode.papertrading.infrastructure.web.dto.CreateOrderRequestDTO;
import com.filipecode.papertrading.infrastructure.web.dto.CreateOrderResponseDTO;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OrderService implements CreateOrderUseCase {

    private final AssetRepositoryPort assetRepositoryPort;
    private final PortfolioRepositoryPort portfolioRepositoryPort;
    private final PriceProviderPort priceProviderPort;
    private final OrderRepositoryPort orderRepositoryPort;
    private final TransactionRepositoryPort transactionRepositoryPort;
    private final PositionRepositoryPort positionRepositoryPort;

    public OrderService(AssetRepositoryPort assetRepositoryPort, PortfolioRepositoryPort portfolioRepositoryPort, PriceProviderPort priceProviderPort, OrderRepositoryPort orderRepositoryPort, TransactionRepositoryPort transactionRepositoryPort, PositionRepositoryPort positionRepositoryPort) {
        this.assetRepositoryPort = assetRepositoryPort;
        this.portfolioRepositoryPort = portfolioRepositoryPort;
        this.priceProviderPort = priceProviderPort;
        this.orderRepositoryPort = orderRepositoryPort;
        this.transactionRepositoryPort = transactionRepositoryPort;
        this.positionRepositoryPort = positionRepositoryPort;
    }

    @Override
    @Transactional
    public CreateOrderResponseDTO createOrder(CreateOrderRequestDTO dto) {
        User user = getAuthenticatedUser();
        Asset asset = findAssetByTicker(dto);
        Portfolio portfolio = findPortfolioByUser(user);


        if (dto.type() == OrderType.BUY) {
            BigDecimal assetValue = priceProviderPort.getCurrentPrice(asset.getTicker());
            BigDecimal totalValue = assetValue.multiply(BigDecimal.valueOf(dto.quantity()));

            validateSufficientFunds(totalValue, portfolio);

            Order order = createAndSaveOrder(dto, portfolio, asset, assetValue);

            registerTransaction(order);

            portfolio.setBalance(portfolio.getBalance().subtract(totalValue));

            updatePosition(dto, portfolio, asset, assetValue);

            portfolioRepositoryPort.save(portfolio);

            return mapToCreateOrderResponse(order);
        }

        throw new UnsupportedOperationException("Tipo de ordem não suportado: " + dto.type());

    }

    private static CreateOrderResponseDTO mapToCreateOrderResponse(Order order) {
        return new CreateOrderResponseDTO(
                order.getId(),
                order.getAsset().getTicker(),
                order.getQuantity(),
                order.getType(),
                order.getMarketOrderType(),
                order.getPrice(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }

    private void updatePosition(CreateOrderRequestDTO dto, Portfolio portfolio, Asset asset, BigDecimal assetValue) {
        Optional<Position> existingPositionOpt = positionRepositoryPort.findByPortfolioAndAsset(portfolio, asset);

        if (existingPositionOpt.isEmpty()) {
            // O usuário NÃO possui este ativo. É a primeira compra.
            Position newPosition = new Position();
            newPosition.setPortfolio(portfolio);
            newPosition.setAsset(asset);
            newPosition.setQuantity(dto.quantity());
            newPosition.setAveragePrice(assetValue); // O preço médio é o próprio preço da primeira compra.
            // Adiciona a nova posição à lista de posições do portfólio.
            portfolio.getPositions().add(newPosition);

        } else {
            // Usuário JÁ possui este ativo.
            Position existingPosition = existingPositionOpt.get();
            // Calcula o valor total da posição antiga e da nova compra
            BigDecimal oldTotalValue = existingPosition.getAveragePrice().multiply(BigDecimal.valueOf(existingPosition.getQuantity()));
            BigDecimal purchaseTotalValue = assetValue.multiply(BigDecimal.valueOf(dto.quantity()));
            // Calcula a nova quantidade total
            int newTotalQuantity = existingPosition.getQuantity() + dto.quantity();
            // Calcula o novo preço médio ponderado
            // (Valor total antigo + valor da nova compra) / (nova quantidade total)
            BigDecimal newAveragePrice = (oldTotalValue.add(purchaseTotalValue))
                    .divide(BigDecimal.valueOf(newTotalQuantity), 2, RoundingMode.HALF_UP);
            // Atualiza a posição existente com os novos valores
            existingPosition.setQuantity(newTotalQuantity);
            existingPosition.setAveragePrice(newAveragePrice);
        }
    }

    private void registerTransaction(Order savedOrder) {
        Transaction newTransaction = Transaction.builder()
                .quantity(savedOrder.getQuantity())
                .price(savedOrder.getPrice())
                .type(savedOrder.getType())
                .timestamp(LocalDateTime.now())
                .portfolio(savedOrder.getPortfolio())
                .asset(savedOrder.getAsset())
                .order(savedOrder)
                .build();

        transactionRepositoryPort.save(newTransaction);
    }

    private Order createAndSaveOrder(CreateOrderRequestDTO dto, Portfolio portfolio, Asset asset, BigDecimal assetValue) {
        Order newOrder = Order.builder()
                .portfolio(portfolio)
                .asset(asset)
                .quantity(dto.quantity())
                .price(assetValue)
                .type(dto.type())
                .marketOrderType(dto.orderType())
                .status(OrderStatus.EXECUTED)
                .build();

        Order savedOrder = orderRepositoryPort.save(newOrder);
        return savedOrder;
    }

    private static void validateSufficientFunds(BigDecimal totalValue, Portfolio portfolio) {
        if (totalValue.compareTo(portfolio.getBalance()) > 0) {
            throw new InsufficientFundsException("Saldo insuficiente!");
        }
    }

    private Portfolio findPortfolioByUser(User user) {
        Portfolio portfolio = portfolioRepositoryPort.findByUser(user)
                .orElseThrow(() -> new PortfolioNotFoundException("Portfólio não encontrado"));
        return portfolio;
    }

    private Asset findAssetByTicker(CreateOrderRequestDTO dto) {
        Asset asset = assetRepositoryPort.findByTicker(dto.ticker())
                .orElseThrow(() -> new AssetNotFoundException("Ativo não encontrado com o ticker: " + dto.ticker()));
        return asset;
    }

    private static User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return user;
    }
}

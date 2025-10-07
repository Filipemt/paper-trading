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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Asset asset = findAssetByTicker(dto.ticker());
        Portfolio portfolio = findPortfolioByUser(user);

        if (dto.type() == OrderType.BUY) {
            return handleBuyOrder(dto, user, asset, portfolio);
        } else if (dto.type() == OrderType.SELL) {
            // Lógica de SELL
            throw new UnsupportedOperationException("Lógica de venda ainda não implementada.");
        }

        throw new IllegalStateException("Tipo de ordem não suportado: " + dto.type());
    }

    private CreateOrderResponseDTO handleBuyOrder(CreateOrderRequestDTO dto, User user, Asset asset, Portfolio portfolio) {
        BigDecimal currentPrice = priceProviderPort.getCurrentPrice(asset.getTicker());
        BigDecimal totalValue = currentPrice.multiply(BigDecimal.valueOf(dto.quantity()));

        executeBuyValidation(portfolio, totalValue);
        Order savedOrder = persistOrderAndTransaction(portfolio, asset, dto, currentPrice);
        updatePortfolioStateForBuy(portfolio, totalValue, dto.quantity(), currentPrice);

        return mapToOrderResponseDTO(savedOrder);
    }

    private Asset findAssetByTicker(String ticker) {
        return assetRepositoryPort.findByTicker(ticker)
                .orElseThrow(() -> new AssetNotFoundException("Ativo não encontrado com o ticker: " + ticker));
    }

    private Portfolio findPortfolioByUser(User user) {
        return portfolioRepositoryPort.findByUser(user)
                .orElseThrow(() -> new PortfolioNotFoundException("Portfólio não encontrado para o usuário."));
    }

    private void executeBuyValidation(Portfolio portfolio, BigDecimal totalValue) {
        if (totalValue.compareTo(portfolio.getBalance()) > 0) {
            throw new InsufficientFundsException("Saldo insuficiente para realizar a compra.");
        }
    }

    private Order persistOrderAndTransaction(Portfolio portfolio, Asset asset, CreateOrderRequestDTO dto, BigDecimal executionPrice) {
        Order newOrder = Order.builder()
                .portfolio(portfolio)
                .asset(asset)
                .quantity(dto.quantity())
                .price(executionPrice)
                .type(dto.type())
                .marketOrderType(dto.orderType())
                .status(OrderStatus.EXECUTED)
                .build();
        Order savedOrder = orderRepositoryPort.save(newOrder);

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

        return savedOrder;
    }

    private void updatePortfolioStateForBuy(Portfolio portfolio, BigDecimal totalValue, int quantity, BigDecimal price) {
        portfolio.setBalance(portfolio.getBalance().subtract(totalValue));

        Optional<Position> existingPositionOpt = positionRepositoryPort.findByPortfolioAndAsset(portfolio, portfolio.getPositions().get(0).getAsset());

        if (existingPositionOpt.isEmpty()) {
            Position newPosition = new Position();
            newPosition.setPortfolio(portfolio);
            newPosition.setAsset(portfolio.getPositions().get(0).getAsset());
            newPosition.setQuantity(quantity);
            newPosition.setAveragePrice(price);
            portfolio.getPositions().add(newPosition);
        } else {
            Position existingPosition = existingPositionOpt.get();
            BigDecimal oldTotalValue = existingPosition.getAveragePrice().multiply(BigDecimal.valueOf(existingPosition.getQuantity()));
            BigDecimal purchaseTotalValue = price.multiply(BigDecimal.valueOf(quantity));
            int newTotalQuantity = existingPosition.getQuantity() + quantity;
            BigDecimal newAveragePrice = (oldTotalValue.add(purchaseTotalValue))
                    .divide(BigDecimal.valueOf(newTotalQuantity), 2, RoundingMode.HALF_UP);

            existingPosition.setQuantity(newTotalQuantity);
            existingPosition.setAveragePrice(newAveragePrice);
        }
        portfolioRepositoryPort.save(portfolio);
    }

    private CreateOrderResponseDTO mapToOrderResponseDTO(Order order) {
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
}
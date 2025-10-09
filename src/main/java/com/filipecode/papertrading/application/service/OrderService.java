package com.filipecode.papertrading.application.service;

import com.filipecode.papertrading.application.usecase.CreateOrderUseCase;
import com.filipecode.papertrading.domain.exception.*;
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

        if (dto.orderType() == MarketOrderType.MARKET) {
            return processMarketOrder(dto, asset, portfolio);
        } else if (dto.orderType() == MarketOrderType.LIMIT) {
            return processLimitOrder(dto, asset, portfolio);
        }

        throw new UnsupportedOrderTypeException("Tipo de ordem não suportado: " + dto.type());
    }


    private CreateOrderResponseDTO processMarketOrder(CreateOrderRequestDTO dto, Asset asset, Portfolio portfolio) {
        BigDecimal currentPrice = priceProviderPort.getCurrentPrice(asset.getTicker());
        BigDecimal totalValue = currentPrice.multiply(BigDecimal.valueOf(dto.quantity()));

        if (dto.type() == OrderType.BUY) {
            if (totalValue.compareTo(portfolio.getBalance()) > 0) {
                throw new InsufficientFundsException("Saldo insuficiente para realizar a compra.");
            }

            Order newOrder = Order.builder()
                    .portfolio(portfolio)
                    .asset(asset)
                    .quantity(dto.quantity())
                    .price(currentPrice)
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

            portfolio.setBalance(portfolio.getBalance().subtract(totalValue));

            Optional<Position> existingPositionOptional = positionRepositoryPort.findByPortfolioAndAsset(portfolio, asset);

            if (existingPositionOptional.isEmpty()) {
                Position newPosition = new Position();
                newPosition.setPortfolio(portfolio);
                newPosition.setAsset(asset);
                newPosition.setQuantity(dto.quantity());
                newPosition.setAveragePrice(currentPrice);
                portfolio.getPositions().add(newPosition);
            }
            else {
                Position existingPosition = existingPositionOptional.get();

                BigDecimal oldTotalValue = existingPosition.getAveragePrice().multiply(BigDecimal.valueOf(existingPosition.getQuantity()));
                BigDecimal purchaseTotalValue = currentPrice.multiply(BigDecimal.valueOf(dto.quantity()));

                int newTotalQuantity = existingPosition.getQuantity() + dto.quantity();
                BigDecimal newAveragePrice = (oldTotalValue.add(purchaseTotalValue))
                        .divide(BigDecimal.valueOf(newTotalQuantity), 2, RoundingMode.HALF_UP);

                existingPosition.setQuantity(newTotalQuantity);
                existingPosition.setAveragePrice(newAveragePrice);
            }
            portfolioRepositoryPort.save(portfolio);

            return mapToOrderResponseDTO(savedOrder);
        }

        else if (dto.type() == OrderType.SELL) {
            Position currentPosition = positionRepositoryPort.findByPortfolioAndAsset(portfolio, asset)
                    .orElseThrow(() -> new PositionNotFoundException("Posição não encontrada."));

            if (currentPosition.getQuantity() < dto.quantity()) {
                throw new InsufficientPositionException("Quantidade de posições insuficiente.");
            }

            currentPrice = priceProviderPort.getCurrentPrice(asset.getTicker());
            BigDecimal totalValueToReceive = currentPrice.multiply(BigDecimal.valueOf(dto.quantity()));

            portfolio.setBalance(portfolio.getBalance().add(totalValueToReceive));
            currentPosition.setQuantity(currentPosition.getQuantity() - dto.quantity());

            Order newOrder = Order.builder()
                    .portfolio(portfolio)
                    .asset(asset)
                    .quantity(dto.quantity())
                    .price(currentPrice)
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

            if (currentPosition.getQuantity() == 0) {
                portfolio.getPositions().remove(currentPosition);
            }

            portfolioRepositoryPort.save(portfolio);

            return mapToOrderResponseDTO(savedOrder);
        }

        throw new UnsupportedOrderTypeException("Tipo de ordem não suportada!");

    }

    private CreateOrderResponseDTO processLimitOrder(CreateOrderRequestDTO dto, Asset asset, Portfolio portfolio) {
        BigDecimal totalAssetsValue = dto.price().multiply(BigDecimal.valueOf(dto.quantity()));

        if (dto.type() == OrderType.BUY) {
            if (totalAssetsValue.compareTo(portfolio.getBalance()) > 0) {
                throw new InsufficientFundsException("Saldo insuficiente para realizar a compra.");
            }

        } else if (dto.type() == OrderType.SELL) {
            Position currentPosition = positionRepositoryPort.findByPortfolioAndAsset(portfolio, asset)
                    .orElseThrow(() -> new PositionNotFoundException("Posição não encontrada."));

            if (currentPosition.getQuantity() < dto.quantity()) {
                throw new InsufficientPositionException("Quantidade de posições insuficiente.");
            }
        }

        Order newOrder = Order.builder()
                .portfolio(portfolio)
                .asset(asset)
                .quantity(dto.quantity())
                .price(dto.price())
                .type(dto.type())
                .marketOrderType(dto.orderType())
                .status(OrderStatus.PENDING)
                .build();

        Order savedOrder = orderRepositoryPort.save(newOrder);
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
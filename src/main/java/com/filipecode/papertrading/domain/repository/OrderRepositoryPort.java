package com.filipecode.papertrading.domain.repository;

import com.filipecode.papertrading.domain.model.trading.Order;
import com.filipecode.papertrading.domain.model.trading.OrderStatus;
import com.filipecode.papertrading.domain.model.trading.OrderType;
import com.filipecode.papertrading.domain.model.user.Portfolio;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepositoryPort {

    Order save(Order order);

    // Operações de leitura no contexto de um portfolio em específico
    Optional<Order> findByIdAndPortfolio(Long id, Portfolio portfolio);

    List<Order> findAllByPortfolio(Portfolio portfolio);
    List<Order> findAllByPortfolioAndType(Portfolio portfolio, OrderType type);
    List<Order> findAllByPortfolioAndStatus(Portfolio portfolio, OrderStatus status);
    List<Order> findAllByPortfolioAndCreatedAtBetween(Portfolio portfolio, LocalDateTime startDate, LocalDateTime endDate);

}

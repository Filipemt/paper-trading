package com.filipecode.papertrading.infrastructure.persistence.jpa.repository.trading;

import com.filipecode.papertrading.domain.model.trading.Order;
import com.filipecode.papertrading.domain.model.trading.OrderStatus;
import com.filipecode.papertrading.domain.model.trading.OrderType;
import com.filipecode.papertrading.domain.model.user.Portfolio;
import com.filipecode.papertrading.domain.repository.OrderRepositoryPort;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class JpaOrderRepositoryAdapter implements OrderRepositoryPort {

    private final JpaOrderRepository jpaRepository;

    public JpaOrderRepositoryAdapter(JpaOrderRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Order save(Order order) {
        return jpaRepository.save(order);
    }

    @Override
    public Optional<Order> findByIdAndPortfolio(Long id, Portfolio portfolio) {
        return jpaRepository.findByIdAndPortfolio(id, portfolio);
    }

    @Override
    public List<Order> findAllByPortfolio(Portfolio portfolio) {
        return jpaRepository.findAllByPortfolio(portfolio);
    }

    @Override
    public List<Order> findAllByPortfolioAndType(Portfolio portfolio, OrderType type) {
        return jpaRepository.findAllByPortfolioAndType(portfolio, type);
    }

    @Override
    public List<Order> findAllByPortfolioAndStatus(Portfolio portfolio, OrderStatus status) {
        return jpaRepository.findAllByPortfolioAndStatus(portfolio, status);
    }

    @Override
    public List<Order> findAllByPortfolioAndCreatedAtBetween(Portfolio portfolio, LocalDateTime startDate, LocalDateTime endDate) {
        return jpaRepository.findAllByPortfolioAndCreatedAtBetween(portfolio, startDate, endDate);
    }
}

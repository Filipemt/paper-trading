package com.filipecode.papertrading.infrastructure.persistence.jpa.repository.trading;

import com.filipecode.papertrading.domain.model.trading.Order;
import com.filipecode.papertrading.domain.model.trading.OrderStatus;
import com.filipecode.papertrading.domain.model.trading.OrderType;
import com.filipecode.papertrading.domain.model.user.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JpaOrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByIdAndPortfolio(Long id, Portfolio portfolio);

    List<Order> findAllByPortfolio(Portfolio portfolio);
    List<Order> findAllByPortfolioAndType(Portfolio portfolio, OrderType type);
    List<Order> findAllByPortfolioAndStatus(Portfolio portfolio, OrderStatus status);
    List<Order> findAllByPortfolioAndCreatedAtBetween(Portfolio portfolio, LocalDateTime startDate, LocalDateTime endDate);
}

package com.filipecode.papertrading.infrastructure.persistence.jpa.repository.trading;

import com.filipecode.papertrading.domain.model.trading.OrderType;
import com.filipecode.papertrading.domain.model.trading.Transaction;
import com.filipecode.papertrading.domain.model.user.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface JpaTransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByIdAndPortfolio(Long id, Portfolio portfolio);

    List<Transaction> findAllByPortfolioOrderByTimestampDesc(Portfolio portfolio);
    List<Transaction> findAllByPortfolioAndTypeOrderByTimestampDesc(Portfolio portfolio, OrderType type);
    List<Transaction> findAllByPortfolioAndTimestampBetweenOrderByTimestampDesc(Portfolio portfolio, LocalDateTime startDate, LocalDateTime endDate);

}

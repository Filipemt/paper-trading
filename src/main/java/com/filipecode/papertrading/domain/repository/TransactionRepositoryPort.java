package com.filipecode.papertrading.domain.repository;

import com.filipecode.papertrading.domain.model.trading.OrderType;
import com.filipecode.papertrading.domain.model.trading.Transaction;
import com.filipecode.papertrading.domain.model.user.Portfolio;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepositoryPort {
    Transaction save(Transaction transaction);

    Optional<Transaction> findByIdAndPortfolio(Long id, Portfolio portfolio);

    List<Transaction> findAllByPortfolioOrderByTimestampDesc(Portfolio portfolio);
    List<Transaction> findAllByPortfolioAndTypeOrderByTimestampDesc(Portfolio portfolio, OrderType type);
    List<Transaction> findAllByPortfolioAndTimestampBetweenOrderByTimestampDesc(Portfolio portfolio, LocalDateTime startDate, LocalDateTime endDate);

}

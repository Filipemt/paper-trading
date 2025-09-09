package com.filipecode.papertrading.infrastructure.persistence.jpa.repository.trading;

import com.filipecode.papertrading.domain.model.trading.OrderType;
import com.filipecode.papertrading.domain.model.trading.Transaction;
import com.filipecode.papertrading.domain.model.user.Portfolio;
import com.filipecode.papertrading.domain.repository.TransactionRepositoryPort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class JpaTransactionRepositoryAdapter implements TransactionRepositoryPort {
    private final JpaTransactionRepository jpaRepository;

    public JpaTransactionRepositoryAdapter(JpaTransactionRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Transaction save(Transaction transaction) {
        return jpaRepository.save(transaction);
    }

    @Override
    public Optional<Transaction> findByIdAndPortfolio(Long id, Portfolio portfolio) {
        return jpaRepository.findByIdAndPortfolio(id, portfolio);
    }

    @Override
    public List<Transaction> findAllByPortfolioOrderByTimestampDesc(Portfolio portfolio) {
        return jpaRepository.findAllByPortfolioOrderByTimestampDesc(portfolio);
    }

    @Override
    public List<Transaction> findAllByPortfolioAndTypeOrderByTimestampDesc(Portfolio portfolio, OrderType type) {
        return jpaRepository.findAllByPortfolioAndTypeOrderByTimestampDesc(portfolio, type);
    }

    @Override
    public List<Transaction> findAllByPortfolioAndTimestampBetweenOrderByTimestampDesc(Portfolio portfolio, LocalDateTime startDate, LocalDateTime endDate) {
        return jpaRepository.findAllByPortfolioAndTimestampBetweenOrderByTimestampDesc(portfolio, startDate, endDate);
    }
}

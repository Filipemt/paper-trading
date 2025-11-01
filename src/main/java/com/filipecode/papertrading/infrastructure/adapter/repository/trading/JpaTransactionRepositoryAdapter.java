package com.filipecode.papertrading.infrastructure.adapter.repository.trading;

import com.filipecode.papertrading.domain.model.trading.Transaction;
import com.filipecode.papertrading.domain.model.user.Portfolio;
import com.filipecode.papertrading.application.port.out.TransactionRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
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
    public Page<Transaction> findAllByPortfolioPaginatedAndOrdered(Portfolio portfolio, Pageable pageable) {
        return jpaRepository.findAllByPortfolioOrderByTimestampDesc(portfolio, pageable);
    }
}

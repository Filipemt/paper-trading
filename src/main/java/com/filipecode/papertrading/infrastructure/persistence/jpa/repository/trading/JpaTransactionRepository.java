package com.filipecode.papertrading.infrastructure.persistence.jpa.repository.trading;

import com.filipecode.papertrading.domain.model.trading.Transaction;
import com.filipecode.papertrading.domain.model.user.Portfolio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface JpaTransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findAllByPortfolioOrderByTimestampDesc(Portfolio portfolio, Pageable pageable);

    Optional<Transaction> findByIdAndPortfolio(Long id, Portfolio portfolio);
}

package com.filipecode.papertrading.domain.repository;

import com.filipecode.papertrading.domain.model.trading.Transaction;
import com.filipecode.papertrading.domain.model.user.Portfolio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TransactionRepositoryPort {
    Transaction save(Transaction transaction);

    Optional<Transaction> findByIdAndPortfolio(Long id, Portfolio portfolio);

    Page<Transaction> findAllByPortfolioPaginatedAndOrdered(Portfolio portfolio, Pageable pageable);

}

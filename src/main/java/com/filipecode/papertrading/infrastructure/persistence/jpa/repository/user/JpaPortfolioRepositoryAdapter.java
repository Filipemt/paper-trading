package com.filipecode.papertrading.infrastructure.persistence.jpa.repository.user;

import com.filipecode.papertrading.domain.model.user.Portfolio;
import com.filipecode.papertrading.domain.model.user.User;
import com.filipecode.papertrading.domain.repository.PortfolioRepositoryPort;

import java.util.Optional;

public class JpaPortfolioRepositoryAdapter implements PortfolioRepositoryPort {

    private final JpaPortfolioRepository jpaRepository;

    public JpaPortfolioRepositoryAdapter(JpaPortfolioRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Portfolio save(Portfolio portfolio) {
        return jpaRepository.save(portfolio);
    }

    @Override
    public Optional<Portfolio> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<Portfolio> findByUser(User user) {
        return jpaRepository.findByUser(user);
    }
}

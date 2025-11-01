package com.filipecode.papertrading.application.port.out;

import com.filipecode.papertrading.domain.model.user.Portfolio;
import com.filipecode.papertrading.domain.model.user.User;

import java.util.Optional;

public interface PortfolioRepositoryPort {
    Portfolio save(Portfolio portfolio);

    Optional<Portfolio> findById(Long id);
    Optional<Portfolio> findByUser(User user);

}

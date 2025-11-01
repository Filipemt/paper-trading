package com.filipecode.papertrading.infrastructure.adapter.repository.user;

import com.filipecode.papertrading.domain.model.user.Portfolio;
import com.filipecode.papertrading.domain.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaPortfolioRepository extends JpaRepository<Portfolio, Long> {
    Optional<Portfolio> findByUser(User user);

}

package com.filipecode.papertrading.infrastructure.adapter.repository.trading;

import com.filipecode.papertrading.domain.model.trading.Order;

import com.filipecode.papertrading.domain.model.user.Portfolio;
import com.filipecode.papertrading.application.port.out.OrderRepositoryPort;
import com.filipecode.papertrading.infrastructure.web.dto.OrderFilterDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class JpaOrderRepositoryAdapter implements OrderRepositoryPort {

    private final JpaOrderRepository jpaRepository;

    public JpaOrderRepositoryAdapter(JpaOrderRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Order save(Order order) {
        return jpaRepository.save(order);
    }

    @Override
    public Optional<Order> findByIdAndPortfolio(Long id, Portfolio portfolio) {
        return jpaRepository.findByIdAndPortfolio(id, portfolio);
    }

    @Override
    public Page<Order> findByCriteria(Portfolio portfolio, OrderFilterDTO filters, Pageable pageable) {
        LocalDateTime startDateTime = (filters.startDate() != null) ? filters.startDate().atStartOfDay() : null;
        LocalDateTime endDateTime = (filters.endDate() != null) ? filters.endDate().atTime(23, 59, 59) : null;

        return jpaRepository.findByCriteria(
                portfolio,
                filters.status(),
                filters.type(),
                startDateTime,
                endDateTime,
                pageable
        );
    }


}

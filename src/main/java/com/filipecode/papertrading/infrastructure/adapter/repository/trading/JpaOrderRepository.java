package com.filipecode.papertrading.infrastructure.adapter.repository.trading;

import com.filipecode.papertrading.domain.model.trading.Order;
import com.filipecode.papertrading.domain.model.trading.OrderStatus;
import com.filipecode.papertrading.domain.model.trading.OrderType;
import com.filipecode.papertrading.domain.model.user.Portfolio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface JpaOrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByIdAndPortfolio(Long id, Portfolio portfolio);

    @Query("""
        SELECT o FROM Order o
        WHERE
            o.portfolio = :portfolio AND
            (:status IS NULL OR o.status = :status) AND
            (:type IS NULL OR o.type = :type) AND
            (:startDate IS NULL OR o.createdAt >= :startDate) AND
            (:endDate IS NULL OR o.createdAt <= :endDate)
    """)
    Page<Order> findByCriteria(
            @Param("portfolio") Portfolio portfolio,
            @Param("status") OrderStatus status,
            @Param("type") OrderType type,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

}

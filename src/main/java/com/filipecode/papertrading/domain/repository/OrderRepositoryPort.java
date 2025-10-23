package com.filipecode.papertrading.domain.repository;

import com.filipecode.papertrading.domain.model.trading.Order;

import com.filipecode.papertrading.domain.model.user.Portfolio;
import com.filipecode.papertrading.infrastructure.web.dto.OrderFilterDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface OrderRepositoryPort {

    Order save(Order order);

    // Operações de leitura no contexto de um portfolio em específico
    Optional<Order> findByIdAndPortfolio(Long id, Portfolio portfolio);

    Page<Order> findByCriteria(Portfolio portfolio, OrderFilterDTO dto, Pageable pageable);

}

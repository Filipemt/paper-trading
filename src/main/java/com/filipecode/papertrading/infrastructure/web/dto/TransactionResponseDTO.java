package com.filipecode.papertrading.infrastructure.web.dto;

import com.filipecode.papertrading.domain.model.trading.OrderType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponseDTO(
        Long id,
        String ticker,
        OrderType type,
        Integer quantity,
        BigDecimal price,
        BigDecimal totalValue, // Calculado: quantity * price
        LocalDateTime timestamp,
        Long orderId
) {
}

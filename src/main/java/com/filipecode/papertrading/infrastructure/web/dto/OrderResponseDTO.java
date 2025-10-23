package com.filipecode.papertrading.infrastructure.web.dto;

import com.filipecode.papertrading.domain.model.trading.MarketOrderType;
import com.filipecode.papertrading.domain.model.trading.OrderStatus;
import com.filipecode.papertrading.domain.model.trading.OrderType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponseDTO(
        Long id,
        String ticker,
        Integer quantity,
        OrderType type,
        MarketOrderType orderType,
        BigDecimal price,
        OrderStatus status,
        LocalDateTime createdAt
) {
}

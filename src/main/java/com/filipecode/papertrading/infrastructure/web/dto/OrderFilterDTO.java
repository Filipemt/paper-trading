package com.filipecode.papertrading.infrastructure.web.dto;

import com.filipecode.papertrading.domain.model.trading.OrderStatus;
import com.filipecode.papertrading.domain.model.trading.OrderType;

import java.time.LocalDate;

public record OrderFilterDTO(
        OrderStatus status,
        OrderType type,
        LocalDate startDate,
        LocalDate endDate
) {
}

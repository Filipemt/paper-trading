package com.filipecode.papertrading.infrastructure.web.dto;

import com.filipecode.papertrading.domain.model.trading.MarketOrderType;
import com.filipecode.papertrading.domain.model.trading.OrderType;
import com.filipecode.papertrading.infrastructure.web.validation.ValidOrderRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@ValidOrderRequest
public record CreateOrderRequestDTO(
        @NotBlank(message = "Ticker não pode ser nulo")
        String ticker,

        @NotNull(message = "A quantidade não pode ser nula")
        @Positive(message = "A quantidade deve ser um número positivo")
        Integer quantity,

        @NotNull(message = "O tipo de ordem não pode ser nulo")
        OrderType type,

        @NotNull(message = "O tipo de execução não pode ser nulo")
        MarketOrderType orderType,

        BigDecimal price
) {
}

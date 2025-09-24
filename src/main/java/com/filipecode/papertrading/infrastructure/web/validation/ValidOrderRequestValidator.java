package com.filipecode.papertrading.infrastructure.web.validation;

import com.filipecode.papertrading.domain.model.trading.MarketOrderType;
import com.filipecode.papertrading.infrastructure.web.dto.CreateOrderRequestDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

public class ValidOrderRequestValidator implements ConstraintValidator<ValidOrderRequest, CreateOrderRequestDTO> {
    @Override
    public boolean isValid(CreateOrderRequestDTO dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return true; // Deixa outras anotações cuidarem do DTO nulo.
        }

        // Esta é a nossa lógica condicional
        if (dto.orderType() == MarketOrderType.LIMIT) {
            // Se for LIMIT, o preço é obrigatório e deve ser maior que zero.
            return dto.price() != null && dto.price().compareTo(BigDecimal.ZERO) > 0;
        }

        // Se for MARKET (ou qualquer outro tipo futuro), a validação passa.
        // O preço será ignorado pelo serviço.
        return true;
    }
}

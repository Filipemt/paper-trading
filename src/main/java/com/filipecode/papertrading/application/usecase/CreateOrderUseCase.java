package com.filipecode.papertrading.application.usecase;

import com.filipecode.papertrading.infrastructure.web.dto.CreateOrderRequestDTO;
import com.filipecode.papertrading.infrastructure.web.dto.CreateOrderResponseDTO;

public interface CreateOrderUseCase {
    CreateOrderResponseDTO createOrder(CreateOrderRequestDTO dto);
}

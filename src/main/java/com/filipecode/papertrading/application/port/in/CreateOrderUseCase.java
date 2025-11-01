package com.filipecode.papertrading.application.port.in;

import com.filipecode.papertrading.infrastructure.web.dto.CreateOrderRequestDTO;
import com.filipecode.papertrading.infrastructure.web.dto.OrderResponseDTO;

public interface CreateOrderUseCase {
    OrderResponseDTO createOrder(CreateOrderRequestDTO dto);
}

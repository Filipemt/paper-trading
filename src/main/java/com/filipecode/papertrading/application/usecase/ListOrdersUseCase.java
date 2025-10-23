package com.filipecode.papertrading.application.usecase;


import com.filipecode.papertrading.infrastructure.web.dto.OrderFilterDTO;
import com.filipecode.papertrading.infrastructure.web.dto.OrderResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListOrdersUseCase {
    Page<OrderResponseDTO> listOrders(OrderFilterDTO dto, Pageable pageable);
}

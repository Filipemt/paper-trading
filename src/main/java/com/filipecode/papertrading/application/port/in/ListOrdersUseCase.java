package com.filipecode.papertrading.application.port.in;


import com.filipecode.papertrading.infrastructure.web.dto.OrderFilterDTO;
import com.filipecode.papertrading.infrastructure.web.dto.OrderResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListOrdersUseCase {
    Page<OrderResponseDTO> listOrders(OrderFilterDTO dto, Pageable pageable);
}

package com.filipecode.papertrading.application.port.in;

import com.filipecode.papertrading.infrastructure.web.dto.PortfolioResponseDTO;

public interface ViewPortfolioUseCase {
    PortfolioResponseDTO view();
}

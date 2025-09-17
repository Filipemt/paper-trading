package com.filipecode.papertrading.domain.repository;

import java.math.BigDecimal;

public interface PriceProviderPort {
    BigDecimal getCurrentPrice(String ticker);
}

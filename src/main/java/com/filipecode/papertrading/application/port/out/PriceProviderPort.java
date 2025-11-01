package com.filipecode.papertrading.application.port.out;

import java.math.BigDecimal;

public interface PriceProviderPort {
    BigDecimal getCurrentPrice(String ticker);
}

package com.filipecode.papertrading.infrastructure.client;

import com.filipecode.papertrading.domain.repository.PriceProviderPort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Profile("dev")
public class SimulatedPriceProviderAdapter implements PriceProviderPort {
    @Override
    public BigDecimal getCurrentPrice(String ticker) {
        return new BigDecimal("15150.75");
    }
}

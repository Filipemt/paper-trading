package com.filipecode.papertrading.infrastructure.client;

import com.filipecode.papertrading.domain.repository.PriceProviderPort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

@Component
@Profile("dev")
public class SimulatedPriceProviderAdapter implements PriceProviderPort {

    private final Random random = new Random();
    @Override
    public BigDecimal getCurrentPrice(String ticker) {
        double randomValue = 10.0 + (190.0 * random.nextDouble());
        return BigDecimal.valueOf(randomValue).setScale(2, RoundingMode.HALF_DOWN);
    }
}

package com.filipecode.papertrading.infrastructure.adapter.client;


import com.filipecode.papertrading.application.port.out.PriceProviderPort;
import com.filipecode.papertrading.infrastructure.adapter.client.dto.BrapiResponseDTO;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Slf4j
public class BrapiPriceProviderAdapter implements PriceProviderPort {
    private final BrapiClient brapiClient;
    private final String apiToken;

    public BrapiPriceProviderAdapter(BrapiClient brapiClient, @Value("${app.brapi.api-token}") String apiToken) {
        this.brapiClient = brapiClient;
        this.apiToken = apiToken;
    }

    @Override
    public BigDecimal getCurrentPrice(String ticker) {
        log.info("Buscando cotação na Brapi para o ticker: {}", ticker);
        try {
            BrapiResponseDTO response = brapiClient.getQuote(ticker, this.apiToken);

            if (response != null && response.results() != null && !response.results().isEmpty()) {
                BigDecimal price = BigDecimal.valueOf(response.results().get(0).regularMarketPrice());
                log.info("Cotação encontrada para {}: {}", ticker, price);
                return price;
            } else {
                log.warn("API da Brapi retornou resposta vazia para o ticker: {}", ticker);
                throw new RuntimeException("Não foi possível obter a cotação do ativo " + ticker);
            }

        } catch (FeignException e) {
            log.error("Erro ao chamar a API da Brapi para o ticker {}: Status {} - {}", ticker, e.status(), e.getMessage());
            throw new RuntimeException("Erro de comunicação com o provedor de cotações.");
        }
    }
}

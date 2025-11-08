package com.filipecode.papertrading.infrastructure.adapter.client;

import com.filipecode.papertrading.infrastructure.adapter.client.dto.BrapiResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "BrapiClient",
        url = "https://brapi.dev"
)
public interface BrapiClient {
    @GetMapping(value =  "/api/quote/{ticker}")
    BrapiResponseDTO getQuote(@PathVariable String ticker,
                              @RequestParam String token);
}

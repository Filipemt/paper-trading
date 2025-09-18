package com.filipecode.papertrading.infrastructure.web.controller;

import com.filipecode.papertrading.application.usecase.ViewPortfolioUseCase;
import com.filipecode.papertrading.infrastructure.web.dto.PortfolioResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/portfolio")
public class PortfolioController {

    private final ViewPortfolioUseCase viewPortfolioUseCase;

    public PortfolioController(ViewPortfolioUseCase viewPortfolioUseCase) {
        this.viewPortfolioUseCase = viewPortfolioUseCase;
    }

    @GetMapping("/me")
    public ResponseEntity<PortfolioResponseDTO> view() {
        return ResponseEntity.ok(viewPortfolioUseCase.view());
    }
}

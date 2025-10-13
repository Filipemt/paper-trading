package com.filipecode.papertrading.infrastructure.web.controller;

import com.filipecode.papertrading.application.usecase.ViewPortfolioUseCase;
import com.filipecode.papertrading.infrastructure.web.dto.ErrorResponseDTO;
import com.filipecode.papertrading.infrastructure.web.dto.PortfolioResponseDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/portfolio")
@Tag(name = "Portfolio Management", description = "Controller para listagem de portfolio ")
@SecurityRequirement(name = "bearerAuth")
public class PortfolioController {

    private final ViewPortfolioUseCase viewPortfolioUseCase;

    public PortfolioController(ViewPortfolioUseCase viewPortfolioUseCase) {
        this.viewPortfolioUseCase = viewPortfolioUseCase;
    }

    @GetMapping("/me")
    @Operation(summary = "Lista o portfolio", description = "Método para listar o portfolio do usuario baseado no token autenticado")
    @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Portfolio retornado com sucesso",
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = PortfolioResponseDTO.class))),
    @ApiResponse(responseCode = "404", description = "Portfolio não encontrado", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = ErrorResponseDTO.class))),
    @ApiResponse(responseCode = "403", description = "Não possui as permissões necessárias para acessar o recurso solicitado",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDTO.class))),
    @ApiResponse(responseCode = "401", description = "Não Autenticado. É necessário enviar um token JWT válido no cabeçalho 'Authorization'.",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDTO.class))),
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor",
                        content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<PortfolioResponseDTO> view() {
        return ResponseEntity.ok(viewPortfolioUseCase.view());
    }
}

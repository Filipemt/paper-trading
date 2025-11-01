package com.filipecode.papertrading.infrastructure.web.controller;

import com.filipecode.papertrading.application.port.in.FindAssetByTickerUseCase;
import com.filipecode.papertrading.application.port.in.ListAssetsUseCase;
import com.filipecode.papertrading.domain.model.asset.AssetType;
import com.filipecode.papertrading.infrastructure.web.dto.AssetResponseDTO;
import com.filipecode.papertrading.infrastructure.web.dto.ErrorResponseDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/assets")
@Tag(name = "Asset Management", description = "Controller para listagem de ativos ")
@SecurityRequirement(name = "bearerAuth")
public class AssetController {

    private final FindAssetByTickerUseCase findAssetByTickerUseCase;
    private final ListAssetsUseCase listAssetsUseCase;

    public AssetController(FindAssetByTickerUseCase findAssetByTickerUseCase, ListAssetsUseCase listAssetsUseCase) {
        this.findAssetByTickerUseCase = findAssetByTickerUseCase;
        this.listAssetsUseCase = listAssetsUseCase;
    }

    @GetMapping("/{ticker}")
    @Operation(summary = "Lista ativo por ticker", description = "Método para listar do ativo especificado"
    )
    @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Ativo retornado com sucesso",
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = AssetResponseDTO.class))),
    @ApiResponse(responseCode = "404", description = "Ativo não encontrado. Ocorre por parâmetros nulos, ou malformados ", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = ErrorResponseDTO.class))),
    @ApiResponse(responseCode = "403", description = "Não possui as permissões necessárias para acessar o recurso solicitado",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDTO.class))),
    @ApiResponse(responseCode = "401",description = "Não Autenticado. É necessário enviar um token JWT válido no cabeçalho 'Authorization'.", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = ErrorResponseDTO.class))),
    @ApiResponse(responseCode = "500", description = "Erro no servidor", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = ErrorResponseDTO.class)))
})
    
    public ResponseEntity<AssetResponseDTO> getAssetByTicker(@Parameter(description = "Ticker do ativo (ex: PETR4, VALE3, ITUB4)", example = "PETR4", required = true) @PathVariable String ticker) {
        AssetResponseDTO responseDTO = findAssetByTickerUseCase.findByTicker(ticker);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    @Operation(summary = "Lista todos os ativos", description = "Método para listar todos os ativos do sistema")
    @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Lista de retornado com sucesso",
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = AssetResponseDTO.class))),
    @ApiResponse(responseCode = "403", description = "Não possui as permissões necessárias para acessar o recurso solicitado",
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = ErrorResponseDTO.class))),
    @ApiResponse(responseCode = "500", description = "Erro no servidor",
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = ErrorResponseDTO.class)))
    })

    public ResponseEntity<Page<AssetResponseDTO>> listAssets(@Parameter(description = "Tipo do ativo (ex: STOCK, FII)", example = "STOCK") @RequestParam(required = false) AssetType type,
                                                             Pageable pageable) {

        Page<AssetResponseDTO> assetPage = listAssetsUseCase.listAll(type, pageable);
        return ResponseEntity.ok(assetPage);
    }
}

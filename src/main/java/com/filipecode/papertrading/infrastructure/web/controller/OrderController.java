package com.filipecode.papertrading.infrastructure.web.controller;

import com.filipecode.papertrading.application.usecase.CreateOrderUseCase;
import com.filipecode.papertrading.application.usecase.CancelOrderUseCase;
import com.filipecode.papertrading.infrastructure.web.dto.CreateOrderRequestDTO;
import com.filipecode.papertrading.infrastructure.web.dto.CreateOrderResponseDTO;
import com.filipecode.papertrading.infrastructure.web.dto.ErrorResponseDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Order Management", description = "Controller para criação de compra e venda de ativos")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final CreateOrderUseCase orderUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;

    public OrderController(CreateOrderUseCase orderUseCase, CancelOrderUseCase cancelOrderUseCase) {
        this.orderUseCase = orderUseCase;
        this.cancelOrderUseCase = cancelOrderUseCase;
    }

    @PostMapping
    @Operation(summary = "Cria uma nova ordem de compra ou venda", description = "Registra uma nova ordem no sistema para o usuário autenticado. Para ordens 'MARKET', a execução é imediata e o portfólio é atualizado. Para ordens 'LIMIT', a ordem é salva com status PENDING.")
    @ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "Ordem criada com sucesso",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CreateOrderResponseDTO.class))
    ),
    @ApiResponse(responseCode = "400", description = "Requisição Inválida. Ocorre por dados de entrada malformados (ex: CPF, e-mail inválido), ou por violação de uma regra de negócio (ex: saldo insuficiente, posição insuficiente).",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDTO.class)) 
    ),
    @ApiResponse(responseCode = "401", description = "Não Autenticado. É necessário enviar um token JWT válido no cabeçalho 'Authorization'.",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDTO.class))
    ),
    @ApiResponse(responseCode = "404", description = "Recurso Não Encontrado. Ocorre se o 'ticker' do ativo informado não existir na plataforma.",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDTO.class))
    ),
    @ApiResponse(responseCode = "500", description = "Erro do servidor",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDTO.class))
    )
})
    public ResponseEntity<CreateOrderResponseDTO> createOrder(@Valid @RequestBody CreateOrderRequestDTO dto) {
        return ResponseEntity.status(201).body(orderUseCase.createOrder(dto));
    }

    @DeleteMapping("/{orderId}")
    @Operation(summary = "Cancela uma ordem pendente", description = "Cancela uma ordem de compra ou venda que ainda não foi executada (status PENDING). O usuário só pode cancelar suas próprias ordens.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Ordem cancelada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não Autenticado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Ordem não encontrada para o usuário.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Conflito. A ordem não pode ser cancelada (ex: já foi executada).",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erro do servidor",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
        cancelOrderUseCase.cancel(orderId);
        return ResponseEntity.noContent().build();
    }
}

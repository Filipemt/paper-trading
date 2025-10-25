package com.filipecode.papertrading.infrastructure.web.controller;

import com.filipecode.papertrading.application.usecase.ListTransactionsUseCase;
import com.filipecode.papertrading.infrastructure.web.dto.TransactionResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag; // Add this import
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transactions")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Transactions", description = "Endpoints para listar o histórico de transações do usuário") // Add Swagger tag
public class TransactionController {

    private final ListTransactionsUseCase listTransactionsUseCase;

    public TransactionController(ListTransactionsUseCase listTransactionsUseCase) {
        this.listTransactionsUseCase = listTransactionsUseCase;
    }

    @GetMapping
    @Operation(summary = "Lista o histórico de transações do usuário autenticado")
    public ResponseEntity<Page<TransactionResponseDTO>> listTransactions(
            @ParameterObject
            @PageableDefault(size = 20, sort = "timestamp", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Page<TransactionResponseDTO> listedTransactions = listTransactionsUseCase.listTransactions(pageable);
        return ResponseEntity.ok(listedTransactions);
    }
}

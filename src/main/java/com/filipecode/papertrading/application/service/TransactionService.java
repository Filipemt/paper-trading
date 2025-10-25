package com.filipecode.papertrading.application.service;

import com.filipecode.papertrading.application.usecase.ListTransactionsUseCase;
import com.filipecode.papertrading.domain.exception.PortfolioNotFoundException;
import com.filipecode.papertrading.domain.model.trading.Transaction;
import com.filipecode.papertrading.domain.model.user.Portfolio;
import com.filipecode.papertrading.domain.model.user.User;
import com.filipecode.papertrading.domain.repository.PortfolioRepositoryPort;
import com.filipecode.papertrading.domain.repository.TransactionRepositoryPort;
import com.filipecode.papertrading.infrastructure.web.dto.TransactionResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransactionService implements ListTransactionsUseCase {

    private final TransactionRepositoryPort transactionRepositoryPort;
    private final PortfolioRepositoryPort portfolioRepositoryPort;

    public TransactionService(TransactionRepositoryPort transactionRepositoryPort, PortfolioRepositoryPort portfolioRepositoryPort) {
        this.transactionRepositoryPort = transactionRepositoryPort;
        this.portfolioRepositoryPort = portfolioRepositoryPort;
    }

    @Override
    public Page<TransactionResponseDTO> listTransactions(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Portfolio portfolio = portfolioRepositoryPort.findByUser(user)
                .orElseThrow(() -> new PortfolioNotFoundException("Portfólio não encontrado para o usuário."));

        // 1. Busca a página de Entidades
        Page<Transaction> transactionPage = transactionRepositoryPort.findAllByPortfolioPaginatedAndOrdered(portfolio, pageable);

        // 2. Usa o .map() da Page para transformar cada Transaction em um TransactionResponseDTO
        return transactionPage.map(this::mapToTransactionResponseDTO);
    }

    private TransactionResponseDTO mapToTransactionResponseDTO(Transaction transaction) {
        BigDecimal totalValue = transaction.getPrice().multiply(BigDecimal.valueOf(transaction.getQuantity())); // Calcula o valor total

        return new TransactionResponseDTO(
                transaction.getId(),
                transaction.getAsset().getTicker(),
                transaction.getType(),
                transaction.getQuantity(),
                transaction.getPrice(),
                totalValue, // Inclui o valor total calculado
                transaction.getTimestamp(),
                transaction.getOrder().getId()
        );
    }
}

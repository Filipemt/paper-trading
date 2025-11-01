package com.filipecode.papertrading.application.port.in;

import com.filipecode.papertrading.infrastructure.web.dto.TransactionResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListTransactionsUseCase {
    Page<TransactionResponseDTO> listTransactions(Pageable pageable);
}

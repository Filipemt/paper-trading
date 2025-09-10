package com.filipecode.papertrading.infrastructure.web.dto;

import java.time.LocalDateTime;

public record ErrorResponseDTO(int status, String message, LocalDateTime timestamp) {
}

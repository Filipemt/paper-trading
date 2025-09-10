package com.filipecode.papertrading.infrastructure.web.dto;

public record AuthResponseDTO(Long userId,
                              String name,
                              String token) {
}

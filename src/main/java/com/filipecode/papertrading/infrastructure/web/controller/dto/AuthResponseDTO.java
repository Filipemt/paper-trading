package com.filipecode.papertrading.infrastructure.web.controller.dto;

public record AuthResponseDTO(Long userId,
                              String name,
                              String token) {
}

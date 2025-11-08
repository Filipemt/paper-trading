package com.filipecode.papertrading.infrastructure.adapter.client.dto;

import java.util.List;

public record BrapiResponseDTO(List<StockDTO> results) {
}

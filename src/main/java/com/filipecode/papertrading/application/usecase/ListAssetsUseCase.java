package com.filipecode.papertrading.application.usecase;

import com.filipecode.papertrading.domain.model.asset.AssetType;
import com.filipecode.papertrading.infrastructure.web.dto.AssetResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListAssetsUseCase {
    Page<AssetResponseDTO> listAll(AssetType type, Pageable pageable);
}

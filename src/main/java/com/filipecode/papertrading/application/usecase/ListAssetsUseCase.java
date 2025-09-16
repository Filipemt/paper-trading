package com.filipecode.papertrading.application.usecase;

import com.filipecode.papertrading.domain.model.asset.Asset;
import com.filipecode.papertrading.domain.model.asset.AssetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListAssetsUseCase {
    Page<Asset> listAll(AssetType type, Pageable pageable);
}

package com.filipecode.papertrading.application.usecase;

import com.filipecode.papertrading.infrastructure.web.dto.AuthResponseDTO;
import com.filipecode.papertrading.infrastructure.web.dto.UpdateUserRequestDTO;

public interface UpdateUserUseCase {
    AuthResponseDTO update(UpdateUserRequestDTO requestData);
}
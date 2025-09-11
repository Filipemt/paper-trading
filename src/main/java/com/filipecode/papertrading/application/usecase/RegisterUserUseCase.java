package com.filipecode.papertrading.application.usecase;

import com.filipecode.papertrading.infrastructure.web.dto.AuthResponseDTO;
import com.filipecode.papertrading.infrastructure.web.dto.RegisterUserRequestDTO;

public interface RegisterUserUseCase {

    AuthResponseDTO register(RegisterUserRequestDTO requestData);
}

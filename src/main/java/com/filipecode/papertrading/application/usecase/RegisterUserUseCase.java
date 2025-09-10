package com.filipecode.papertrading.application.usecase;

import com.filipecode.papertrading.infrastructure.web.controller.dto.AuthResponseDTO;
import com.filipecode.papertrading.infrastructure.web.controller.dto.RegisterUserRequestDTO;

public interface RegisterUserUseCase {

    AuthResponseDTO execute(RegisterUserRequestDTO requestData);
}

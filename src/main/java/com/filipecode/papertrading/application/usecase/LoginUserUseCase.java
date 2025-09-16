package com.filipecode.papertrading.application.usecase;

import com.filipecode.papertrading.infrastructure.web.dto.AuthResponseDTO;
import com.filipecode.papertrading.infrastructure.web.dto.LoginUserRequestDTO;

public interface LoginUserUseCase {
    AuthResponseDTO login(LoginUserRequestDTO requestData);
}

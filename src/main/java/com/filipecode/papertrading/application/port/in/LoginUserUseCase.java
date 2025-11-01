package com.filipecode.papertrading.application.port.in;

import com.filipecode.papertrading.infrastructure.web.dto.AuthResponseDTO;
import com.filipecode.papertrading.infrastructure.web.dto.LoginUserRequestDTO;

public interface LoginUserUseCase {
    AuthResponseDTO login(LoginUserRequestDTO requestData);
}

package com.filipecode.papertrading.application.port.in;

import com.filipecode.papertrading.infrastructure.web.dto.AuthResponseDTO;
import com.filipecode.papertrading.infrastructure.web.dto.RegisterUserRequestDTO;

public interface RegisterUserUseCase {

    AuthResponseDTO register(RegisterUserRequestDTO requestData);
}

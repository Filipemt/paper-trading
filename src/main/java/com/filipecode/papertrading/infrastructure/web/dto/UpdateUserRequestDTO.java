package com.filipecode.papertrading.infrastructure.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateUserRequestDTO(
        @Size(min = 2, message = "O nome deve ter no mínimo 2 caracteres.")
        String name,

        @Email(message = "E-mail fornecido não é válido.")
        String email,

        @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres.")
        String password
) {
}
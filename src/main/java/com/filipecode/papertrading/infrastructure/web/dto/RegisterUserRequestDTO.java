package com.filipecode.papertrading.infrastructure.web.dto;

import com.filipecode.papertrading.infrastructure.web.validation.CPF;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserRequestDTO(
        @NotBlank(message = "O nome não pode estar em branco.")
        String name,

        @NotBlank(message = "O E-mail não pode estar em branco.")
        @Email(message = "E-mail fornecido não é válido.")
        String email,

        @NotBlank(message = "A senha não pode estar em branco.")
        @Size(min = 8, message = "Senha deve possuir no mínimo 8 caracteres.")
        String password,

        @NotBlank(message = "CPF não pode estar em branco.")
        @CPF(message = "Informe um CPF válido.")
        String cpf
) {
}
